package com.riis.towerpower.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.riis.towerpower.R;

/**
 * @author tkocikjr
 */
public class RowClickListener implements View.OnClickListener
{
    private Context mContext;
    private int mTitleId;
    private int mMessageId;

    public RowClickListener(Context context, int titleId, int messageId)
    {
        mContext = context;
        mTitleId = titleId;
        mMessageId = messageId;
    }

    @Override
    public void onClick(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mTitleId);
        builder.setMessage(mMessageId);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }
}
