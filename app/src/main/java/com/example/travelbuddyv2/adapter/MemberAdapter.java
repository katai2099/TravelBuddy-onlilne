package com.example.travelbuddyv2.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbuddyv2.PermissionModificationActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.model.Member;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberHolder> {

    List<Member> memberList;
    MemberAdapterCallBack memberAdapterCallBack;
    boolean isMember ;

    public MemberAdapter(List<Member> memberList,MemberAdapterCallBack memberAdapterCallBack,boolean isMember) {
        this.memberList = memberList;
        this.memberAdapterCallBack = memberAdapterCallBack;
        this.isMember = isMember;
    }

    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_row,parent,false);
        return new MemberHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberHolder holder, final int position) {

        Member member = memberList.get(position);
        holder.imgMemberProfile.setImageResource(R.drawable.ic_baseline_person_24);
        holder.tvMemberName.setText(member.getName());
        holder.tvMemberEmail.setText(member.getEmail());
        if(member.getPermission().equals("owner")){
            holder.tvMemberPermission.setText("edit");
            holder.tvMemberOwnership.setText(member.getPermission());
            holder.btnMemberDelete.setVisibility(View.GONE);
        }
        else{
            holder.tvMemberPermission.setText(member.getPermission());
        }
        if(member.getProfileImg()!=null && !(TextUtils.isEmpty(member.getProfileImg()))){
            Picasso.get().load(member.getProfileImg()).fit().into(holder.imgMemberProfile);
        }

        if(isMember){
            holder.btnMemberDelete.setVisibility(View.GONE);
        }
        holder.btnMemberDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberAdapterCallBack.onMemberDeleteClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    class MemberHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imgMemberProfile;
        final TextView tvMemberName, tvMemberEmail , tvMemberPermission, tvMemberOwnership;
        final Button btnMemberDelete;

        public MemberHolder(@NonNull View itemView) {
            super(itemView);

            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberEmail = itemView.findViewById(R.id.tvMemberEmail);
            tvMemberPermission = itemView.findViewById(R.id.tvMemberPermission);
            tvMemberOwnership = itemView.findViewById(R.id.tvMemberOwnership);
            imgMemberProfile = itemView.findViewById(R.id.imgMemberProfile);
            btnMemberDelete = itemView.findViewById(R.id.btnMemberDelete);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
          memberAdapterCallBack.onMemberListClicked(getAdapterPosition());
        }
    }

    public interface MemberAdapterCallBack{
        void onMemberListClicked(int position);
        void onMemberDeleteClicked(int position);
    }

}
