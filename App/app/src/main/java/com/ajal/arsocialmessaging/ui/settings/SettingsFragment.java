package com.ajal.arsocialmessaging.ui.settings;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.databinding.FragmentSettingsBinding;
import com.ajal.arsocialmessaging.util.PermissionHelper;
import com.ajal.arsocialmessaging.util.PostcodeHelper;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Check that SkyWrite has the correct permissions and if not, request them
        if (!PermissionHelper.hasPermissions(this.getActivity())) {
            Toast.makeText(this.getContext(), "Permissions are needed to run this application", Toast.LENGTH_LONG).show();
            PermissionHelper.requestPermissionsIfDenied(this.getActivity());
            return null;
        }

        TextView postcodeView = (TextView) root.findViewById(R.id.text_currentPostcode);
        Location location = PostcodeHelper.getLocation(this.getContext());
        String postcode = PostcodeHelper.getPostCode(this.getContext(), location.getLatitude(), location.getLongitude());
        postcodeView.setText(postcodeView.getText()+postcode);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}