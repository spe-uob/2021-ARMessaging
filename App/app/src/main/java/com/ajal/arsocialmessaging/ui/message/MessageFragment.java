package com.ajal.arsocialmessaging.ui.message;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ajal.arsocialmessaging.CustomArrayAdapter;
import com.ajal.arsocialmessaging.util.ConnectivityHelper;
import com.ajal.arsocialmessaging.util.HashCreator;
import com.ajal.arsocialmessaging.util.database.server.ServerDBObserver;
import com.ajal.arsocialmessaging.util.database.server.MessageService;
import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.server.ServerDBHelper;
import com.ajal.arsocialmessaging.util.database.Message;
import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.util.database.server.ServiceGenerator;
import com.ajal.arsocialmessaging.databinding.FragmentMessageBinding;
import com.ajal.arsocialmessaging.util.location.PostcodeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends Fragment implements ServerDBObserver {

    private FragmentMessageBinding binding;

    // Components on fragment
    private List<String> messages;
    private ListView listView;
    private TextView postCodeInput;
    private Button sendBtn;
    private String messageSelected = "";
    private int messageSelectedId = 1;
    private String postCode;
    private static final String TAG = "SkyWrite";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Check if network is available
        if (!ConnectivityHelper.getInstance().isNetworkAvailable()) {
            Toast.makeText(this.getContext(), "Network error. Please try again", Toast.LENGTH_SHORT);
            return root;
        }

        // Request the server to load the results from the database
        ServerDBHelper serverDbHelper = ServerDBHelper.getInstance();
        // Need to clear callbacks or else ServerDBHelper can try to send a context which no longer exists
        ServerDBHelper.getInstance().clearObservers();
        serverDbHelper.registerObserver(this);
        serverDbHelper.retrieveDBResults();

        /** Postcode button code */
        postCodeInput = root.findViewById(R.id.text_input_postcode);
        sendBtn = root.findViewById(R.id.send_button);

        // Adds a listener to postcode text input to enable/disable button
        postCodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setSendBtnAvailability(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setSendBtnAvailability(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        sendBtn.setOnClickListener(view -> {
            String input = postCodeInput.getText().toString();
            String formattedInput = PostcodeHelper.formatPostcode(postCodeInput.getText().toString());
            if (PostcodeHelper.checkPostcodeValid(formattedInput)) {
                postCode = formattedInput;
                Toast.makeText(getContext(), "Sent \""+messageSelected+"\" to: "+postCode, Toast.LENGTH_SHORT).show();
                addBannerToDatabase(postCode);
            }
            else {
                Toast.makeText(getContext(), "Invalid postcode: "+input, Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    /**
     * Enables/disables the send button, depending on postCode and messageSelected
     */
    private void setSendBtnAvailability(String text) {
        if(!text.isEmpty() && !messageSelected.isEmpty()){
            sendBtn.setEnabled(true);
        } else {
            sendBtn.setEnabled(false);
        }
    }

    /**
     * Manages database connection when user sends a message
     */
    private void addBannerToDatabase(String postcode){
        // Set up connection for app to talk to database via rest controller
        MessageService service = ServiceGenerator.createService(MessageService.class);
        String hashedPostcode = HashCreator.createSHAHash(postcode);
        String bannerData = hashedPostcode + "," + messageSelectedId; // hashes the postcode before sending to server
        Call<String> addBannerCall = service.addBanner(bannerData);
        addBannerCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String postResponse = response.body();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Unable to send message, please try again", Toast.LENGTH_SHORT);
                call.cancel();
            }
        });

    }

    /**
     * Populates listView with images and messages once they are received from the database
     */
    @Override
    public void onMessageSuccess(List<Message> result) {

        /** ListView with images */
        // Add images to ArrayList to be displayed in listView
        List<Integer> imageid = new ArrayList<>();
        imageid.add(R.drawable.happy_birthday);
        imageid.add(R.drawable.merry_christmas);
        imageid.add(R.drawable.congratulations);
        imageid.add(R.drawable.good_luck);
        imageid.add(R.drawable.get_well_soon);
        imageid.add(R.drawable.thank_you);

        // Populate listview with images and text
        View root = binding.getRoot();
        messages = ServerDBHelper.getInstance().getMessages().stream().map(Message::getMessage).collect(Collectors.toList());
        listView = root.findViewById(R.id.list_messagesToSend);
        CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity(), messages, imageid);
        listView.setAdapter(adapter);

        // Sets a listener to figure out which item was clicked in list view
        listView.setOnItemClickListener((parent, view, position, id) -> {
            messageSelected = parent.getItemAtPosition(position).toString();
            Log.d("MYTAG", "Position in list is: "+position);
            messageSelectedId = position+1;  // Offset by 1 since DB records start at 1 and positions start at 0
            String text = postCodeInput.getText().toString();
            setSendBtnAvailability(text);   // Toggle send button depending on text input
        });

    }

    @Override
    public void onMessageFailure() {
        Log.e(TAG, "Error receiving messages");
        Toast.makeText(this.getContext(), "Error receiving messages", Toast.LENGTH_SHORT);
    }

    @Override
    public void onBannerSuccess(List<Banner> result) {
    }

    @Override
    public void onBannerFailure() {
        Log.e(TAG, "Error receiving messages");
        Toast.makeText(this.getContext(), "Error receiving banners", Toast.LENGTH_SHORT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}