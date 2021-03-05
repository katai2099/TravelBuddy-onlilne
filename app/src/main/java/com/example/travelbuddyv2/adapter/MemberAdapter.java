package com.example.travelbuddyv2.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.PermissionModifyActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Member;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberHolder> {

    List<Member> memberList;

    public MemberAdapter(List<Member> memberList) {
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_row,parent,false);
        return new MemberHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberHolder holder, int position) {

        Member member = memberList.get(position);
        holder.tvMemberName.setText(member.getName());
        holder.tvMemberEmail.setText(member.getEmail());
        holder.tvMemberPermission.setText(member.getPermission());

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    class MemberHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView tvMemberName, tvMemberEmail , tvMemberPermission;

        public MemberHolder(@NonNull View itemView) {
            super(itemView);

            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberEmail = itemView.findViewById(R.id.tvMemberEmail);
            tvMemberPermission = itemView.findViewById(R.id.tvMemberPermission);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), PermissionModifyActivity.class);
            v.getContext().startActivity(i);
        }
    }

}
