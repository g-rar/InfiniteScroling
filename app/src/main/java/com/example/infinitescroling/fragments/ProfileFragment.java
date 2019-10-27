
package com.example.infinitescroling.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.infinitescroling.EditProfileActivity;
import com.example.infinitescroling.LoginActivity;
import com.example.infinitescroling.R;


public class ProfileFragment extends Fragment {

    private int MODIFY_ACCOUNT = 1;
    private int ACCOUNT_DELETED = 6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        view.findViewById(R.id.button_editProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileOnClick(v);
            }
        });
        return view;
    }

    public void editProfileOnClick(View view){
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivityForResult(intent, MODIFY_ACCOUNT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MODIFY_ACCOUNT){
            if(resultCode == ACCOUNT_DELETED){
                Intent loginIntent = new Intent(this.getContext(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();
            }
        }
    }
}
