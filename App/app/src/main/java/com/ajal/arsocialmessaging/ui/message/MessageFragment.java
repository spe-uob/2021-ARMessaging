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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ajal.arsocialmessaging.Banner;
import com.ajal.arsocialmessaging.Message;
import com.ajal.arsocialmessaging.MessageService;
import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.ServiceGenerator;
import com.ajal.arsocialmessaging.databinding.FragmentMessageBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends Fragment {

    private FragmentMessageBinding binding;

    // Components on fragment
    private List<String> messages;
    private ListView listView;
    private TextView postCodeInput;
    private Button sendBtn;
    private String messageSelected = "";
    private String postCode;


    private void addBannerToDatabase(String postcode, String message){
        // Set up connection for app to talk to database via rest controller
        MessageService service = ServiceGenerator.createService(MessageService.class);
        // TODO: have some way of converting message chosen to messageId, currently just hardcoded to 1
        Banner bannerToSend = new Banner(postcode, 1);
        Call<String> call1 = service.addBanner(bannerToSend);
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                //String postResponse = response.body();
                //Log.d("MYTAG", "Response: "+postResponse);
                Log.d("MYTAG", "Got a response, error is "+response.errorBody()+" "+response.message());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "onFailure called ", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /** ListView code */
        // Fills the ListView with messages
        messages = Arrays.asList(getResources().getStringArray(R.array.messages));
        listView = root.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(adapter);

        // Sets a listener to figure out what item was clicked in list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                messageSelected = parent.getItemAtPosition(position).toString();
                String text = postCodeInput.getText().toString();
                setSendBtnAvailability(text);
            }
        });

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

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postCode = postCodeInput.getText().toString();
                Toast.makeText(getContext(), postCode+": "+messageSelected, Toast.LENGTH_SHORT).show();
                addBannerToDatabase(postCode, messageSelected);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}