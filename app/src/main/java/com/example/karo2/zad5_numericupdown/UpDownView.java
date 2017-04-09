package com.example.karo2.zad5_numericupdown;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by Karo2 on 2017-04-09.
 */

public class UpDownView extends LinearLayout {

    private final static int REPEAT_DELAY = 50;
    private Handler mAutoUpdateHandler;
    private boolean mAutoIncrement, mAutoDecrement;

    private LinearLayout mLayout;
    private ImageButton mUpButton, mDownButton;
    private EditText mEditText;

    private int mButtonOrientation;
    private float mMin, mMax, mValue, mStep;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public UpDownView(Context context) {
        super(context);
        init(context, null, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public UpDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public UpDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UpDownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleRes);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void init(Context context, AttributeSet attrs, int defStyle) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.up_down_view, this, true);

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UpDownView, defStyle, 0);
        try {
            mLayout = (LinearLayout) getChildAt(0);
            mUpButton = (ImageButton) mLayout.getChildAt(0);
            mEditText = (EditText) mLayout.getChildAt(1);
            mDownButton = (ImageButton) mLayout.getChildAt(2);

            mButtonOrientation = a.getInteger( R.styleable.UpDownView_btnOrientation, 0);
            setLayoutOrientation();

            mMin = a.getFloat(R.styleable.UpDownView_min, 0);
            mMax = a.getFloat(R.styleable.UpDownView_max, 100);
            mValue = a.getFloat(R.styleable.UpDownView_value, 1);
            setEditText();
            mStep = a.getFloat(R.styleable.UpDownView_step, 1);

            if (a.hasValue(R.styleable.UpDownView_btnDown)) {
                mDownButton.setBackground(a.getDrawable(R.styleable.UpDownView_btnDown));
            }
            if (a.hasValue(R.styleable.UpDownView_editTxt)) {
                mEditText.setBackground(a.getDrawable(R.styleable.UpDownView_editTxt));
            }
            if (a.hasValue(R.styleable.UpDownView_btnUp)) {
                mUpButton.setBackground(a.getDrawable(R.styleable.UpDownView_btnUp));
            }
            initUI();
        }
        finally {
            a.recycle();
        }
    }

    private void initUI() {
        mAutoDecrement = false;
        mAutoIncrement = false;
        mAutoUpdateHandler = new Handler();

        class AutoUpdater implements Runnable {

            @Override
            public void run() {
                if(mAutoIncrement) {
                    increaseValue();
                    setEditText();
                    mAutoUpdateHandler.postDelayed(new AutoUpdater(), REPEAT_DELAY);
                }
                else if (mAutoDecrement) {
                    decreaseValue();
                    setEditText();
                    mAutoUpdateHandler.postDelayed(new AutoUpdater(), REPEAT_DELAY);
                }
            }
        }

        mUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseValue();
                setEditText();
            }
        });

        mUpButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mAutoIncrement = true;
                mAutoUpdateHandler.post(new AutoUpdater());
                return false;
            }
        });

        mUpButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP || motionEvent.getAction()==MotionEvent.ACTION_CANCEL) {
                    mAutoIncrement = false;
                }
                return false;
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    float userValue = Float.parseFloat(mEditText.getText().toString());
                    if (userValue < mMin) {
                        mValue = mMin;
                        setEditText();
                    } else if (userValue > mMax) {
                        mValue = mMax;
                        setEditText();
                    }
                    else {
                        mValue = userValue;
                    }
                }
                catch(Exception e) {

                }
            }
        });

        mDownButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseValue();
                setEditText();
            }
        });

        mDownButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mAutoDecrement = true;
                mAutoUpdateHandler.post(new AutoUpdater());
                return false;
            }
        });

        mDownButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_CANCEL || motionEvent.getAction()==MotionEvent.ACTION_UP) {
                    mAutoDecrement = false;
                }
                return false;
            }
        });
    }

    private void setLayoutOrientation() {
        if(mButtonOrientation==1) {
            mLayout.setOrientation(HORIZONTAL);
        }
        else {
            mLayout.setOrientation(VERTICAL);
        }
    }

    private void setEditText() {
        mEditText.setText(String.format("%.2f", mValue));
    }

    private void increaseValue() {
        mValue += mStep;
        if(mValue>mMax) {
            mValue = mMax;
        }
    }

    private void decreaseValue() {
        mValue -= mStep;
        if(mValue<mMin) {
            mValue = mMin;
        }
    }
}
