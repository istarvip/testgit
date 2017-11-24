package com.walnutin.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.walnutin.hard.R;

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    private Context context;
    private String title;
    private String confirmButtonText;
    private String cancelButtonText;
    private String okButtonText;
    private ICancelListener cancelListener;
    private IOkListener okListener;

    public ConfirmDialog(Context context, String title, String confirmButtonText, String cacelButtonText, String okButtonText) {
        super(context, R.style.myDialog);
        this.context = context;
        this.title = title;
        this.confirmButtonText = confirmButtonText;
        this.cancelButtonText = cacelButtonText;
        this.okButtonText = okButtonText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.customdialog, null);
        setContentView(view);

        TextView tvTitle = (TextView) view.findViewById(R.id.dialog_title);
        TextView tvContent = (TextView) view.findViewById(R.id.dialog_content);
        TextView tvCancel = (TextView) view.findViewById(R.id.cancel);
        TextView tvOk = (TextView) view.findViewById(R.id.ok);
        tvTitle.setText(title);
        tvContent.setText(confirmButtonText);
        tvCancel.setText(cancelButtonText);
        tvOk.setText(okButtonText);

        tvCancel.setOnClickListener(new clickListener());
        tvOk.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.9);
        dialogWindow.setAttributes(lp);
    }

    public void setCancellistener(ICancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setOkListener(IOkListener okListener) {
        this.okListener = okListener;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
                case R.id.cancel:
                    if(cancelListener!=null) {
                        cancelListener.doCancel();
                    }
                    break;
                case R.id.ok:
                    if(okListener!=null) {
                        okListener.doOk();
                    }
                    break;
            }
        }

    }

    ;

    public static interface ICancelListener {
        void doCancel();
    }

    public static interface IOkListener {
        void doOk();
    }


}
