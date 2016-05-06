package com.example.keyboardtest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

/**
 * Вспомогательный класс, для отображения пустого вью
 */
public class EmptyKeyboardView extends PopupWindow  {
    private int keyBoardHeight = 0;
    private Boolean pendingOpen = false;
    private Boolean isOpened = false;
    private OnSoftKeyboardOpenCloseListener onSoftKeyboardOpenCloseListener;
    private View rootView;
    private Context mContext;

    /**
     * @param rootView	Родительское вью, которое будет использоваться для расчёта размера экрана и следовательно расчёта высоты клавиатуры
     * @param mContext Контекст активити
     */
    public EmptyKeyboardView(View rootView, Context mContext){
        super(mContext);
        this.mContext = mContext;
        this.rootView = rootView;
        View customView = createCustomView();
        setContentView(customView);
        setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //стандартный размер
        setSize(250, LayoutParams.MATCH_PARENT);
    }
    /**
     * Слушатель, который будет получать события о открытии/закрытии клавиатуры
     */
    public void setOnSoftKeyboardOpenCloseListener(OnSoftKeyboardOpenCloseListener listener){
        this.onSoftKeyboardOpenCloseListener = listener;
    }

    /**
     * Вывод пустого вью на экран снизу, на место клавиатуры
     */
    public void showAtBottom() {
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * @return Возвращает true, если клавиатура открыта
     */
    public Boolean isKeyBoardOpen(){
        return isOpened;
    }

    /**
     * Закрывает пустое вью
     */
    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * Этот метод рассчитывает высоту клавиатуры и передает значения слушателю, если клавиатура закрыта
     */
    public void setSizeForSoftKeyboard(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int screenHeight = getUsableScreenHeight();
                int heightDifference = screenHeight
                        - (r.bottom - r.top);
                int resourceId = mContext.getResources()
                        .getIdentifier("status_bar_height",
                                "dimen", "android");
                if (resourceId > 0) {
                    heightDifference -= mContext.getResources()
                            .getDimensionPixelSize(resourceId);
                }
                if (heightDifference > 100) {
                    keyBoardHeight = heightDifference;
                    setSize(LayoutParams.MATCH_PARENT, keyBoardHeight);
                    if(isOpened == false){
                        if(onSoftKeyboardOpenCloseListener!=null)
                            onSoftKeyboardOpenCloseListener.onKeyboardOpen(keyBoardHeight);
                    }
                    isOpened = true;
                    if(pendingOpen){
                        showAtBottom();
                        pendingOpen = false;
                    }
                }
                else{
                    isOpened = false;
                    if(onSoftKeyboardOpenCloseListener!=null)
                        onSoftKeyboardOpenCloseListener.onKeyboardClose();
                }
            }
        });
    }


    /**
     * @return высота экрана
     */
    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();

            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;

        } else {
            return rootView.getRootView().getHeight();
        }
    }

    /**
     * Ручное задание размера пустого вью
     * @param width ширина
     * @param height высота
     */
    public void setSize(int width, int height){
        setWidth(width);
        setHeight(height);
    }

    /**
     * Создание вью из layout
     */
    private View createCustomView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_view, null, false);
        return view;
    }

    public interface OnSoftKeyboardOpenCloseListener{
        void onKeyboardOpen(int keyBoardHeight);
        void onKeyboardClose();
    }
}
