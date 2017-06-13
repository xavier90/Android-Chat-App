package edu.scu.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by yaojianwang on 6/12/17.
 */

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Message> mChatList;
    public static final int SENDER = 0;
    public static final int RECIPIENT = 1;
    public static final int text = 2;
    public static final int image = 22;
    private Context context;

    public MessageListAdapter(List<Message> listOfFireChats, Context context) {
        mChatList = listOfFireChats;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(mChatList.get(position).getRecipientOrSenderStatus()==SENDER){
            return SENDER;
        }else {
            return RECIPIENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int resultViewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());


        switch (resultViewType) {
            case 2: //sender text
                View viewSenderText = inflater.inflate(R.layout.text_message_sender, viewGroup, false);
                viewHolder= new ViewHolderSenderText(viewSenderText);
                break;
            case 22: //sender image
                View viewSenderImage = inflater.inflate(R.layout.image_message_sender, viewGroup, false);
                viewHolder= new ViewholderSenderImage(viewSenderImage);
                break;
            case 3: //recipient text
                View viewRecipientText = inflater.inflate(R.layout.text_message_recipient, viewGroup, false);
                viewHolder=new ViewHolderRecipientText(viewRecipientText);
                break;
            case 23: //recipient image
                View viewRecipientImage = inflater.inflate(R.layout.image_message_recipient, viewGroup, false);
                viewHolder=new ViewHolderRecipientImage(viewRecipientImage);
                break;
            default:
                View viewSenderDefault = inflater.inflate(R.layout.text_message_sender, viewGroup, false);
                viewHolder= new ViewHolderSenderText(viewSenderDefault);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()){
            case 2:
                ViewHolderSenderText viewHolderSenderText=(ViewHolderSenderText) viewHolder;
                configureSenderView(viewHolderSenderText,position);
                break;
            case 22:
                ViewholderSenderImage viewholderSenderImage=(ViewholderSenderImage) viewHolder;
                configureSenderView(viewholderSenderImage,position, context);
                break;
            case 3:
                ViewHolderRecipientText viewHolderRecipientText=(ViewHolderRecipientText) viewHolder;
                configureRecipientView(viewHolderRecipientText,position);
                break;
            case 23:
                ViewHolderRecipientImage viewHolderRecipientImage=(ViewHolderRecipientImage) viewHolder;
                configureRecipientView(viewHolderRecipientImage,position, context);
                break;
        }


    }

    private void configureSenderView(ViewHolderSenderText viewHolderSenderText, int position) {
        Message senderMessageText= mChatList.get(position);
        viewHolderSenderText.getSenderMessageTextView().setText(senderMessageText.getText());
    }

    //use glide to load image url to imageview
    private void configureSenderView(ViewholderSenderImage viewHolderSenderImage, int position, Context context) {
        Message senderMessageImage= mChatList.get(position);
        Glide.with(context).load(senderMessageImage.getImageUrl()).into(viewHolderSenderImage.getSenderMessageImageView());
    }

    private void configureRecipientView(ViewHolderRecipientText viewHolderRecipientText, int position) {
        Message recipientFireMessageText = mChatList.get(position);
        viewHolderRecipientText.getRecipientMessageTextView().setText(recipientFireMessageText.getText());
    }

    private void configureRecipientView(ViewHolderRecipientImage viewHolderRecipientImage, int position, Context context) {
        Message recipientMessageImage= mChatList.get(position);
        Glide.with(context).load(recipientMessageImage.getImageUrl()).into(viewHolderRecipientImage.mRecipientMessageImageView);
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }


    public void refillAdapter(Message newFireChatMessage){

        /*add new message chat to list*/
        mChatList.add(newFireChatMessage);

        /*refresh view*/
        notifyItemInserted(getItemCount()-1);
    }


    public void cleanUp() {
        mChatList.clear();
    }


    /*==============ViewHolder===========*/

    /*ViewHolder for Sender*/

    public class ViewHolderSenderText extends RecyclerView.ViewHolder {

        private TextView mSenderMessageTextView;

        public ViewHolderSenderText(View itemView) {
            super(itemView);
            mSenderMessageTextView =(TextView)itemView.findViewById(R.id.txt_content);
        }

        public TextView getSenderMessageTextView() {
            return mSenderMessageTextView;
        }

    }

    public class ViewholderSenderImage extends RecyclerView.ViewHolder {
        private ImageView mSenderMessageImageView;

        public ViewholderSenderImage(View itemView) {
            super(itemView);
            mSenderMessageImageView = (ImageView) itemView.findViewById(R.id.img_user_image);
        }

        public ImageView getSenderMessageImageView() {
            return mSenderMessageImageView;
        }
    }


    /*ViewHolder for Recipient*/
    public class ViewHolderRecipientText extends RecyclerView.ViewHolder {

        private TextView mRecipientMessageTextView;

        public ViewHolderRecipientText(View itemView) {
            super(itemView);
            mRecipientMessageTextView=(TextView)itemView.findViewById(R.id.txt_content);
        }

        public TextView getRecipientMessageTextView() {
            return mRecipientMessageTextView;
        }

    }

    public class ViewHolderRecipientImage extends RecyclerView.ViewHolder {

        private ImageView mRecipientMessageImageView;

        public ViewHolderRecipientImage(View itemView) {
            super(itemView);
            mRecipientMessageImageView=(ImageView)itemView.findViewById(R.id.img_user_image);
        }

        public ImageView getRecipientMessageTextView() {
            return mRecipientMessageImageView;
        }

    }
}
