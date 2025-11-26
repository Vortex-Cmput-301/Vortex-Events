package com.example.vortex_events;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * LotteryManager handles random sampling of entrants for an event.
 *
 * Design (team discussion):
 *  - Draw from the "waitlist" array on the Event document.
 *  - Invited users are NOT removed from waitlist.
 *  - Invited users are only added to a new "invited" array.
 *  - "accepted" / "declined" are updated later when users respond.
 *
 * Firestore structure used here:
 *  Collection: "Events"
 *    Document: {eventId}
 *      - capacity        : number (Long)
 *      - waitlist        : array of userIds (String)
 *      - accepted        : array of userIds (String)
 *      - declined        : array of userIds (String)
 *      - invited         : array of userIds (String)  <-- added/updated here
 *      - enrollement_end : String (end of enrollment period, used as draw time)
 *      - lastLotterySize : number        (optional, for debugging)
 *      - lastLotteryTime : timestamp     (optional, for debugging)
 */
public class LotteryManager {

    private static final String TAG = "LotteryManager";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Callback for initial / automatic lottery draw.
     */
    public interface LotteryCallback {
        /**
         * @param invitedIds userIds that were newly invited in THIS run
         *                   (does not include users invited in previous runs)
         */
        void onSuccess(List<String> invitedIds);
        void onFailure(Exception e);
    }

    /**
     * Callback for user response (accept / decline).
     */
    public interface ResponseCallback {
        /**
         * @param currentAccepted all userIds currently in accepted after the update
         * @param currentDeclined all userIds currently in declined after the update
         * @param newlyInvited    userIds that were newly added to invited
         *                        as replacement(s) in this call (may be empty)
         */
        void onSuccess(List<String> currentAccepted,
                       List<String> currentDeclined,
                       List<String> newlyInvited);

        void onFailure(Exception e);
    }

    /**
     * Run the initial (or additional) draw for an event.
     *
     * Rules:
     *  - Draw from the "waitlist" array on the Event document.
     *  - Users already in accepted / declined / invited are excluded from the pool.
     *  - Invited users stay on the waitlist. We do NOT remove them.
     *  - We only append newly drawn userIds to the "invited" array.
     *
     * @param eventId      Firestore document id in "Events" collection.
     * @param seatsToDraw  How many people the organizer wants to invite in this run.
     * @param callback     Result callback.
     */
    public static void runInitialDraw(
            @NonNull String eventId,
            int seatsToDraw,
            @NonNull LotteryCallback callback
    ) {
        if (seatsToDraw <= 0) {
            callback.onFailure(new IllegalArgumentException("seatsToDraw must be > 0"));
            return;
        }

        final DocumentReference eventRef = db.collection("Events").document(eventId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snap = transaction.get(eventRef);
            if (!snap.exists()) {
                throw new IllegalStateException("Event not found: " + eventId);
            }

            // capacity (may be null, so give it a big default)
            Long capacityLong = snap.getLong("capacity");
            int capacity = capacityLong != null ? capacityLong.intValue() : Integer.MAX_VALUE;

            // Read existing arrays with safe defaults
            List<String> accepted = (List<String>) snap.get("accepted");
            List<String> waitlist = (List<String>) snap.get("waitlist");
            List<String> declined = (List<String>) snap.get("declined");
            List<String> invited = (List<String>) snap.get("invited");

            if (accepted == null) accepted = new ArrayList<>();
            if (waitlist == null) waitlist = new ArrayList<>();
            if (declined == null) declined = new ArrayList<>();
            if (invited == null) invited = new ArrayList<>();

            // How many seats are still available based on capacity and accepted size?
            int seatsRemaining = Math.max(0, capacity - accepted.size());
            if (seatsRemaining == 0) {
                // No space at all, nothing to do.
                return new ArrayList<String>();
            }

            int actualDrawSize = Math.min(seatsToDraw, seatsRemaining);

            // Pool = users in waitlist who are NOT already accepted / declined / invited.
            Set<String> excluded = new HashSet<>();
            excluded.addAll(accepted);
            excluded.addAll(declined);
            excluded.addAll(invited);

            List<String> pool = new ArrayList<>();
            for (String uid : waitlist) {
                if (uid != null && !excluded.contains(uid)) {
                    pool.add(uid);
                }
            }

            if (pool.isEmpty()) {
                // no one to invite
                return new ArrayList<String>();
            }

            // Randomize pool
            Collections.shuffle(pool);

            // Take up to actualDrawSize users from pool
            List<String> drawn = new ArrayList<>();
            int limit = Math.min(actualDrawSize, pool.size());
            for (int i = 0; i < limit; i++) {
                drawn.add(pool.get(i));
            }

            // invited_new = invited_old + drawn (keep previous invites)
            List<String> newInvited = new ArrayList<>(invited);
            newInvited.addAll(drawn);

            Map<String, Object> updates = new HashMap<>();
            updates.put("invited", newInvited);
            updates.put("lastLotterySize", drawn.size());
            updates.put("lastLotteryTime", new Date());

            // Merge to keep other fields untouched
            transaction.set(eventRef, updates, SetOptions.merge());

            // This list will be passed to onSuccess()
            return drawn;
        }).addOnSuccessListener(invitedIds -> {
            Log.d(TAG, "Lottery success, invited: " + invitedIds);
            callback.onSuccess(invitedIds);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lottery failed", e);
            callback.onFailure(e);
        });
    }

    /**
     * Automatically trigger a lottery IF:
     *  - Current time is AFTER enrollement_end, AND
     *  - The event has NOT been drawn yet (invited is empty or null), AND
     *  - There is still remaining capacity.
     *
     * Rules for this "auto" version:
     *  - Draw size = remaining seats = capacity - accepted.size().
     *  - If anything is wrong (time not reached, parsing error, etc.), it simply returns.
     *  - This method only logs the result; UI code can ignore it safely.
     *
     * NOTE:
     *  - This does NOT send notifications. That part is handled by teammates.
     *  - This method is safe to call multiple times; it will only draw once
     *    because we check if "invited" is already non-empty.
     */
    public static void triggerDrawIfTimeReached(@NonNull String eventId) {
        final DocumentReference eventRef = db.collection("Events").document(eventId);

        eventRef.get().addOnSuccessListener(snap -> {
            if (!snap.exists()) {
                Log.w(TAG, "triggerDrawIfTimeReached: event not found: " + eventId);
                return;
            }

            // 1. Read enrollement_end as String
            String endString = snap.getString("enrollement_end");
            if (endString == null || endString.trim().isEmpty()) {
                Log.w(TAG, "triggerDrawIfTimeReached: enrollement_end is empty for event " + eventId);
                return;
            }

            // 2. Parse it to Date
            //    Pattern should match what you used when creating the event,
            //    e.g. "yyyy-MM-dd HH:mm" or similar.
            Date endTime;
            try {
                // Adjust the pattern if your actual format is different.
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                endTime = sdf.parse(endString.trim());
            } catch (ParseException e) {
                Log.w(TAG, "triggerDrawIfTimeReached: failed to parse enrollement_end: " + endString, e);
                return;
            }

            if (endTime == null) {
                Log.w(TAG, "triggerDrawIfTimeReached: endTime is null after parsing for event " + eventId);
                return;
            }

            Date now = new Date();
            if (now.before(endTime)) {
                // Not yet time to draw.
                Log.d(TAG, "triggerDrawIfTimeReached: current time is before enrollement_end, skip.");
                return;
            }

            // 3. Check if we already have invited users -> if yes, do NOT draw again.
            List<String> invited = (List<String>) snap.get("invited");
            if (invited != null && !invited.isEmpty()) {
                Log.d(TAG, "triggerDrawIfTimeReached: lottery already executed (invited not empty).");
                return;
            }

            // 4. Read capacity and accepted to compute remaining seats.
            Long capacityLong = snap.getLong("capacity");
            int capacity = capacityLong != null ? capacityLong.intValue() : Integer.MAX_VALUE;

            List<String> accepted = (List<String>) snap.get("accepted");
            if (accepted == null) accepted = new ArrayList<>();

            int seatsRemaining = Math.max(0, capacity - accepted.size());
            if (seatsRemaining <= 0) {
                Log.d(TAG, "triggerDrawIfTimeReached: no remaining seats, skip lottery.");
                return;
            }

            // 5. Run the actual draw using remaining seats as draw size.
            runInitialDraw(eventId, seatsRemaining, new LotteryCallback() {
                @Override
                public void onSuccess(List<String> invitedIds) {
                    Log.d(TAG, "triggerDrawIfTimeReached: lottery executed automatically, invited: " + invitedIds);
                    // No UI / notification here; handled elsewhere by teammates if needed.
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "triggerDrawIfTimeReached: lottery failed", e);
                }
            });

        }).addOnFailureListener(e -> {
            Log.e(TAG, "triggerDrawIfTimeReached: failed to read event " + eventId, e);
        });
    }

    /**
     * Handle a user's response to an invitation.
     *
     * This is used for US 02.05.03:
     *  - A user who is in "invited" chooses Accept or Decline.
     *  - If Accept:
     *        - Add userId to "accepted"
     *        - Remove userId from "declined" (if present)
     *        - Remove userId from "invited"
     *  - If Decline:
     *        - Add userId to "declined"
     *        - Remove userId from "accepted" (if present)
     *        - Remove userId from "invited"
     *
     *  After updating the user's status, if there is remaining capacity,
     *  we automatically draw replacement(s) from the waitlist:
     *    - Pool = waitlist - accepted - declined - invited
     *    - Randomly pick up to "seatsRemaining" users and add them to "invited"
     *
     *  We do NOT remove anyone from waitlist (consistent with team design).
     *
     * @param eventId         Event document id.
     * @param userId          The user who responds.
     * @param acceptedResponse true if user accepts the invitation; false if declines.
     * @param callback        Result callback.
     */
    public static void handleUserResponse(
            @NonNull String eventId,
            @NonNull String userId,
            boolean acceptedResponse,
            @NonNull ResponseCallback callback
    ) {
        final DocumentReference eventRef = db.collection("Events").document(eventId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snap = transaction.get(eventRef);
            if (!snap.exists()) {
                throw new IllegalStateException("Event not found: " + eventId);
            }

            Long capacityLong = snap.getLong("capacity");
            int capacity = capacityLong != null ? capacityLong.intValue() : Integer.MAX_VALUE;

            List<String> accepted = (List<String>) snap.get("accepted");
            List<String> waitlist = (List<String>) snap.get("waitlist");
            List<String> declined = (List<String>) snap.get("declined");
            List<String> invited = (List<String>) snap.get("invited");

            if (accepted == null) accepted = new ArrayList<>();
            if (waitlist == null) waitlist = new ArrayList<>();
            if (declined == null) declined = new ArrayList<>();
            if (invited == null) invited = new ArrayList<>();

            // 1. Update this user's status
            if (acceptedResponse) {
                if (!accepted.contains(userId)) {
                    accepted.add(userId);
                }
                declined.remove(userId);
            } else {
                if (!declined.contains(userId)) {
                    declined.add(userId);
                }
                accepted.remove(userId);
            }
            invited.remove(userId); // user has responded, so no longer "pending invited"

            // 2. Compute remaining seats
            int seatsRemaining = Math.max(0, capacity - accepted.size());
            List<String> newlyInvited = new ArrayList<>();

            if (seatsRemaining > 0) {
                // 3. Build pool for replacement draw:
                //    users in waitlist but NOT in accepted / declined / invited
                Set<String> excluded = new HashSet<>();
                excluded.addAll(accepted);
                excluded.addAll(declined);
                excluded.addAll(invited); // still invited ones (haven't responded yet)

                List<String> pool = new ArrayList<>();
                for (String uid : waitlist) {
                    if (uid != null && !excluded.contains(uid)) {
                        pool.add(uid);
                    }
                }

                if (!pool.isEmpty()) {
                    Collections.shuffle(pool);

                    int limit = Math.min(seatsRemaining, pool.size());
                    for (int i = 0; i < limit; i++) {
                        String newInvite = pool.get(i);
                        newlyInvited.add(newInvite);
                        invited.add(newInvite);
                    }
                }
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("accepted", accepted);
            updates.put("declined", declined);
            updates.put("invited", invited);
            updates.put("lastLotterySize", newlyInvited.size());
            updates.put("lastLotteryTime", new Date());

            transaction.set(eventRef, updates, SetOptions.merge());

            // Return a small result object
            Map<String, Object> result = new HashMap<>();
            result.put("accepted", new ArrayList<>(accepted));
            result.put("declined", new ArrayList<>(declined));
            result.put("newlyInvited", newlyInvited);

            return result;
        }).addOnSuccessListener(resultObj -> {
            try {
                @SuppressWarnings("unchecked")
                List<String> acceptedRes = (List<String>) resultObj.get("accepted");
                @SuppressWarnings("unchecked")
                List<String> declinedRes = (List<String>) resultObj.get("declined");
                @SuppressWarnings("unchecked")
                List<String> newlyInvitedRes = (List<String>) resultObj.get("newlyInvited");

                if (acceptedRes == null) acceptedRes = new ArrayList<>();
                if (declinedRes == null) declinedRes = new ArrayList<>();
                if (newlyInvitedRes == null) newlyInvitedRes = new ArrayList<>();

                callback.onSuccess(acceptedRes, declinedRes, newlyInvitedRes);
            } catch (ClassCastException e) {
                Log.e(TAG, "handleUserResponse: cast error", e);
                callback.onFailure(e);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "handleUserResponse: transaction failed", e);
            callback.onFailure(e);
        });
    }
}
