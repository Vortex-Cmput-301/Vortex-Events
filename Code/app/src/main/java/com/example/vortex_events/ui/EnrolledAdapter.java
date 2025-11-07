package com.example.vortex_events.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vortex_events.R;
import com.example.vortex_events.model.EnrolledUser;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EnrolledAdapter extends ListAdapter<EnrolledUser, EnrolledAdapter.VH> {

    public EnrolledAdapter() {
        super(DIFF);
    }

    private static final DiffUtil.ItemCallback<EnrolledUser> DIFF =
            new DiffUtil.ItemCallback<EnrolledUser>() {
                @Override public boolean areItemsTheSame(@NonNull EnrolledUser a, @NonNull EnrolledUser b) {
                    return a.id.equals(b.id);
                }
                @Override public boolean areContentsTheSame(@NonNull EnrolledUser a, @NonNull EnrolledUser b) {
                    Timestamp at = a.enrolledAt, bt = b.enrolledAt;
                    long al = at==null?0:at.toDate().getTime();
                    long bl = bt==null?0:bt.toDate().getTime();
                    String an = a.name==null?"":a.name, bn = b.name==null?"":b.name;
                    return al==bl && an.equals(bn);
                }
            };

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enrolled_user, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int position) {
        EnrolledUser u = getItem(position);
        h.txtName.setText(u.name == null || u.name.isEmpty() ? u.id : u.name);
        if (u.enrolledAt == null) {
            h.txtTime.setText("Enrolled");
        } else {
            Date d = u.enrolledAt.toDate();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            h.txtTime.setText("Enrolled — " + f.format(d));
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView txtName, txtTime;
        VH(View v) {
            super(v);
            txtName = v.findViewById(R.id.txtName);
            txtTime = v.findViewById(R.id.txtTime);
        }
    }
}
