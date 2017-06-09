package edu.scu.chat.Utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import edu.scu.chat.R;

/**
 * Created by yaojianwang on 6/7/17.
 */

public class DialogUtils {

    /** A popup to select the type of message to send, "Text", "Image", "Location".*/
    public static PopupWindow getMenuOptionPopup(Context context, View.OnClickListener listener){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_options, null);
        PopupWindow optionPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupView.findViewById(R.id.btn_choose_picture).setOnClickListener(listener);
        popupView.findViewById(R.id.btn_take_picture).setOnClickListener(listener);
        popupView.findViewById(R.id.btn_location).setOnClickListener(listener);

        if (!Utils.Options.LocationEnabled || context.getString(R.string.google_maps_api_key).isEmpty()){
            popupView.findViewById(R.id.btn_location).setVisibility(View.GONE);
        }

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        // TODO fix popup size to wrap view size.
        optionPopup.setContentView(popupView);
        optionPopup.setBackgroundDrawable(new BitmapDrawable());
        optionPopup.setOutsideTouchable(true);
        optionPopup.setWidth(popupView.getMeasuredWidth());
        optionPopup.setHeight(popupView.getMeasuredHeight());
        return optionPopup;
    }



}
