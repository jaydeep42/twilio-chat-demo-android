package com.twilio.chat.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.Member;
import com.twilio.chat.Message;
import com.twilio.chat.User;

import org.json.JSONObject;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.list_item_chat_message)
public class MessageViewHolder extends ItemViewHolder<MessageActivity.MessageItem> {

    private static final String TAG = "MessageViewHolder";

    private static int[] HORIZON_COLORS = {
            Color.GRAY, Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA
    };

//    @ViewId(R.id.avatar)
//    ImageView imageView;
//
//    @ViewId(R.id.reachability)
//    ImageView reachabilityView;

    @ViewId(R.id.body2)
    TextView body;

//    @ViewId(R.id.author)
//    TextView author;
//
    @ViewId(R.id.dateee)
    TextView date;
//
//    @ViewId(R.id.consumptionHorizonIdentities)
//    RelativeLayout identities;
//
    @ViewId(R.id.lay_parent)
    LinearLayout lines;
//
    @ViewId(R.id.layout)
    LinearLayout authorLine;


    View view;

    public MessageViewHolder(View view) {
        super(view);
        this.view = view;
    }

    @Override
    public void onSetListeners() {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                OnMessageClickListener listener = getListener(OnMessageClickListener.class);
                if (listener != null) {
                    listener.onMessageClicked(getItem());
                    return true;
                }
                return false;
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.setVisibility(date.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onSetValues(final MessageActivity.MessageItem message, PositionInfo pos) {
        if (message != null) {
            final Message msg = message.getMessage();

//            author.setText(msg.getAuthor());

            date.setText(msg.getTimeStamp());

//            identities.removeAllViews();
//            lines.removeAllViews();

            for (Member member : message.getMembers().getMembersList()) {
                if (msg.getAuthor().equals(member.getIdentity())) {
//                    fillUserAvatar(imageView, member);
//                    fillUserReachability(reachabilityView, member);
//                    authorLine.setGravity(Gravity.START);
                    body.setText(msg.getAuthor() +": " + msg.getMessageBody());
                    lines.setGravity(Gravity.LEFT);
                    authorLine.setBackgroundResource(R.drawable.bubble_a);

                }
                else{
//                    authorLine.setGravity(Gravity.END);

                    body.setText("You: " + msg.getMessageBody());
                    lines.setGravity(Gravity.RIGHT);
                    authorLine.setBackgroundResource(R.drawable.bubble_b);


                }
//
//                if (member.getLastConsumedMessageIndex() != null
//                        && member.getLastConsumedMessageIndex()
//                        == message.getMessage().getMessageIndex()) {
//                    drawConsumptionHorizon(member);
//
//
//                }
            }
        }
    }


//    private void setAlignment(ViewHolder holder, boolean isMe) {
//        if (!isMe) {
////                holder.contentWithBG.setBackgroundResource(R.drawable.ic_launcher_round);
//
//            LinearLayout.LayoutParams layoutParams =
//                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
//            layoutParams.gravity = Gravity.LEFT;
//            holder.contentWithBG.setLayoutParams(layoutParams);
//
//            RelativeLayout.LayoutParams lp =
//                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
//            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
//            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            holder.content.setLayoutParams(lp);
//            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
//            layoutParams.gravity = Gravity.LEFT;
//            holder.txtMessage.setLayoutParams(layoutParams);
//
//            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
//            layoutParams.gravity = Gravity.LEFT;
//            holder.txtInfo.setLayoutParams(layoutParams);
//        } else {
////                holder.contentWithBG.setBackgroundResource(R.drawable.ic_launcher_round);
//
//            LinearLayout.LayoutParams layoutParams =
//                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
//            layoutParams.gravity = Gravity.RIGHT;
//            holder.contentWithBG.setLayoutParams(layoutParams);
//
//            RelativeLayout.LayoutParams lp =
//                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
//            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
//            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            holder.content.setLayoutParams(lp);
//            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
//            layoutParams.gravity = Gravity.RIGHT;
//            holder.txtMessage.setLayoutParams(layoutParams);
//
//            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
//            layoutParams.gravity = Gravity.RIGHT;
//            holder.txtInfo.setLayoutParams(layoutParams);
//        }
//    }


    private void drawConsumptionHorizon(Member member) {
        String ident = member.getIdentity();
        int color = getMemberRgb(ident);

        TextView identity = new TextView(getContext());
        identity.setText(ident);
        identity.setTextSize(8);
        identity.setTextColor(color);

        // Layout
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        int cc = identities.getChildCount();
//        if (cc > 0) {
//            params.addRule(RelativeLayout.RIGHT_OF, identities.getChildAt(cc - 1).getId());
//        }
        identity.setLayoutParams(params);

        View line = new View(getContext());
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));
        line.setBackgroundColor(color);

//        identities.addView(identity);
//        lines.addView(line);
    }

    private void fillUserAvatar(final ImageView avatarView, Member member) {
        TwilioApplication.get().getBasicClient().getChatClient().getUsers().getAndSubscribeUser(member.getIdentity(), new CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                JSONObject attributes = user.getAttributes();
                String avatar = (String) attributes.opt("avatar");
                if (avatar != null) {
                    byte[] data = Base64.decode(avatar, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    avatarView.setImageBitmap(bitmap);
                } else {
                    avatarView.setImageResource(R.drawable.avatar2);
                }
            }
        });
    }

    private void fillUserReachability(final ImageView reachabilityView, Member member) {
        if (!TwilioApplication.get().getBasicClient().getChatClient().isReachabilityEnabled()) {
            reachabilityView.setImageResource(R.drawable.reachability_disabled);
        } else {
            TwilioApplication.get().getBasicClient().getChatClient().getUsers().getAndSubscribeUser(member.getIdentity(), new CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    if (user.isOnline()) {
                        reachabilityView.setImageResource(R.drawable.reachability_online);
                        Log.e(TAG, "onSuccess online: "+reachabilityView );
                    } else if (user.isNotifiable()) {
                        reachabilityView.setImageResource(R.drawable.reachability_notifiable);
                        Log.e(TAG, "onSuccess noti: "+reachabilityView );
                    } else {
                        reachabilityView.setImageResource(R.drawable.reachability_offline);
                        Log.e(TAG, "onSuccess offline: "+reachabilityView );
                    }
                }
            });
        }
    }

    public int getMemberRgb(String identity) {
        return HORIZON_COLORS[Math.abs(identity.hashCode()) % HORIZON_COLORS.length];
    }

    public interface OnMessageClickListener {
        void onMessageClicked(MessageActivity.MessageItem message);
    }
}
