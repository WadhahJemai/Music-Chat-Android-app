package chat.messaging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praktikum.R;
import com.example.praktikum.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter <ChatAdapter.ViewHolder>{
    private Context mContext;
    private List<User> users;

    public ChatAdapter(Context mContext, List<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.user_item_chat,parent,false);
        return  new ChatAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
             final User user=users.get(position);
             holder.username.setText(user.getUsername());
        Picasso.with(mContext).load(user.getUserUri()).fit().centerCrop().into(holder.profile_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(mContext,ChatActivity.class);
                intent.putExtra("id",user.getId());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public  static  class  ViewHolder extends  RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;


        public ViewHolder(View view){
            super(view);
            username=view.findViewById(R.id.username_chat);
            profile_image=view.findViewById(R.id.profile_image_chat);
        }
    }
}
