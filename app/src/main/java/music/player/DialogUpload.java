package music.player;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.praktikum.R;

public class DialogUpload extends AppCompatDialogFragment {

    TextView textView;
    ImageButton imageButtonNo;
    ImageButton imageButtonOk;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.layout_dialog,null);
        builder.setView(view);
        textView=view.findViewById(R.id.texttttt);
        imageButtonNo=view.findViewById(R.id.ne_id);
        imageButtonOk=view.findViewById(R.id.ja_id);

        imageButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MusicFragment()).commit();
                dismiss();
            }
        });

        imageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return  builder.create();
    }

}
