package com.ajal.arsocialmessaging.ui.message;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.databinding.FragmentMessageBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageFragment extends Fragment {

    private MessageViewModel messageViewModel;
    private FragmentMessageBinding binding;

    private String messageSelected;
    private String postCode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        messageViewModel =
                new ViewModelProvider(this).get(MessageViewModel.class);

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMessage;
        messageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        /** ListView code */
        // Fills the ListView with messages
        List<String> messages = Arrays.asList(getResources().getStringArray(R.array.messages));
        ListView listView = root.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(adapter);

        // Sets a listener to figure out what item was clicked in list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                messageSelected = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), messageSelected, Toast.LENGTH_SHORT).show(); // used to debug
            }
        });

        /** Postcode button code */
        TextView postCodeInput = root.findViewById(R.id.text_input_postcode);
        Button sendBtn = root.findViewById(R.id.send_button);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postCode = postCodeInput.getText().toString();
                Toast.makeText(getContext(), postCode, Toast.LENGTH_SHORT).show();
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