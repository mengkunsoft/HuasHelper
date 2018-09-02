package cn.mkblog.huashelper.tool;
 
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.mkblog.huashelper.R;

/**
 * 自定义 dialog
 * Create custom Dialog windows for your application
 * Custom dialogs rely on custom layouts wich allow you to 
 * create and use your own look & feel.
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * @author antoine vianey
 *
 */
public class MkDialog extends Dialog {

    // 定义 Dialog 输入回调事件
    public interface OnDialogInputListener{
        public void dialogInput(String str);
    }

    public MkDialog(Context context, int theme) {
        super(context, theme);
    }
 
    public MkDialog(Context context) {
        super(context);
    }
 
    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
 
        private Context context;
        private String title;
        private String message;
        private String input, inputHint;    // 输入框
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;

        private EditText dialogInput;   // dialog 内容输入框

        private OnClickListener
                        positiveButtonClickListener,
                        negativeButtonClickListener;

        private OnDialogInputListener dialogInputListener;  // dialog 输入回调监听

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog message from String
         * @param message dialog中显示的内容
         * @return this
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         * @param message 来自 Res 的dialog内容
         * @return this
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         * @param title 设置来自资源的dialog标题
         * @return this
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * @param title 设置dialog标题
         * @return this
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置一个带有输入框的 DIALOG
         * @param input 输入框默认内容
         * @param inputHint 提示语
         * @return this
         */
        public Builder setInput(String input, String inputHint) {
            this.input = input;
            this.inputHint = inputHint;
            return this;
        }

        /**
         * 设置一个自定义的中部内容区  Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v View
         * @return this
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * 从资源文件中设置默认按钮以及绑定
         * Set the positive button resource and it's listener
         * @param positiveButtonText 按钮
         * @param listener 监听器
         * @return this
         */
        public Builder setPositiveButton(int positiveButtonText,
                OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * 从字符串中设置积极按钮及绑定
         * Set the positive button text and it's listener
         * @param positiveButtonText 按钮
         * @param listener 监听
         * @return this
         */
        public Builder setPositiveButton(String positiveButtonText,
                OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * 从字符串中设置输入按钮点击事件绑定
         * Set the positive button text and it's listener
         * @param positiveButtonText 按钮
         * @param listener 监听
         * @return this
         */
        public Builder setPositiveButton(String positiveButtonText,
                                         OnDialogInputListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.dialogInputListener = listener;
            return this;
        }

        /**
         * 从资源中设置消极按钮及绑定
         * Set the negative button resource and it's listener
         * @param negativeButtonText 消极按钮
         * @param listener 按钮监听
         * @return this
         */
        public Builder setNegativeButton(int negativeButtonText,
                OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * 从字符串中设置消极按钮及绑定
         * Set the negative button text and it's listener
         * @param negativeButtonText 积极按钮
         * @param listener 按钮监听
         * @return this
         */
        public Builder setNegativeButton(String negativeButtonText,
                OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public MkDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final MkDialog dialog = new MkDialog(context,
            		R.style.MkDialog);
            dialog.setCanceledOnTouchOutside(false);
            assert inflater != null;
            View layout = inflater.inflate(R.layout.mk_dialog_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            dialogInput = (EditText) layout.findViewById(R.id.mk_dialog_input);

            // 设置标题  set the dialog title
            if (title != null) {
                ((TextView) layout.findViewById(R.id.mk_dialog_title)).setText(title);
            } else {
                // 无标题则隐藏标题栏 if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.mk_dialog_head).setVisibility(
                        View.GONE);
            }

            // 设置输入框
            if (input != null) {
                ((TextView) layout.findViewById(R.id.mk_dialog_input)).setText(input);

                if(inputHint != null) {
                    ((TextView) layout.findViewById(R.id.mk_dialog_input)).setHint(inputHint);
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.mk_dialog_input).setVisibility(
                        View.GONE);
            }

            // 设置确定按钮  set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    layout.findViewById(R.id.positiveButton)
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(
                                    		dialog,
                                            DialogInterface.BUTTON_POSITIVE
                                            );
                                }
                            });
                } else if (dialogInputListener != null) {
                    layout.findViewById(R.id.positiveButton)
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialogInputListener.dialogInput(dialogInput.getText().toString());
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }

            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    layout.findViewById(R.id.negativeButton)
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    negativeButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }

            // 设置消息区域内容 set the content message
            if (message != null) {
                ((TextView) layout.findViewById(
                		R.id.mk_dialog_msg)).setText(message);
            } else if (contentView != null) {
                // 如果没有设置消息内容  if no message set
                // 显示自定义的内容区   add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.mk_dialog_content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.mk_dialog_content))
                        .addView(contentView, 
                                new LayoutParams(
                                        LayoutParams.WRAP_CONTENT,
                                        LayoutParams.WRAP_CONTENT));
            } else {
                layout.findViewById(R.id.mk_dialog_msg).setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }
 
    }
 
}