package viethoa.material;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MaterialButton extends RelativeLayout {

    //----------------------------------------------
    // Animation
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static Context mContext;

    private MaterialListener listener;
    private int ANIMATION_DURATION;

    private int defDuration = 200;
    private float defScaleTo = 1f;
    private float defScaleFrom = 0.1f;

    private float rectangle = 0.5f;
    private float longRectangle = 1f;
    private float startScaleWithEffectPosition = 0.2f;

    private boolean isActive;
    private float scaleXFrom;
    private float scaleYFrom;
    private float scaleXTo;
    private float scaleYTo;
    private float alpha;

    private float rootX = 0;
    private float rootY = 0;
    private boolean effectPosition = false;

    private boolean eventClick = true;

    //--------------------
    // UI
    public TextView btnMaterial;
    public View bgMaterial;

    private String text;
    private float textSize;
    private Number textColor;
    private Number effectColor;
    private Drawable drawableCenter;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private Drawable drawableTop;
    private Drawable drawableBottom;
    private Number drawablePadding;

    private final int getDrawableNull = 0;

    //---------------------------------------------------------------------
    // Default
    //---------------------------------------------------------------------

    public static interface MaterialListener {
        public abstract void onClick();
    }

    public MaterialButton(Context context) {
        super(context);
        mContext = context;

        initView(context);
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initialiseAttrs(context, attrs);
        initView(context);
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        initialiseAttrs(context, attrs);
        initView(context);
    }

    //---------------------------------------------------------------------
    // Attributes
    //---------------------------------------------------------------------

    private void initialiseAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MaterialButton);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {

            int attr = a.getIndex(i);
            if (attr == R.styleable.MaterialButton_text) {
                text = a.getString(R.styleable.MaterialButton_text);

            } else if (attr == R.styleable.MaterialButton_textSize) {
                textSize = a.getFloat(R.styleable.MaterialButton_textSize, 18);

            } else if (attr == R.styleable.MaterialButton_textColor) {
                textColor = a.getColor(R.styleable.MaterialButton_textColor, android.R.color.black);

            } else if (attr == R.styleable.MaterialButton_effectColor) {
                effectColor = a.getColor(R.styleable.MaterialButton_effectColor, R.color.bg_overlay_material_button);

            } else if (attr == R.styleable.MaterialButton_eventClick) {
                eventClick = a.getBoolean(R.styleable.MaterialButton_eventClick, true);

            } else if (attr == R.styleable.MaterialButton_effectPosition) {
                effectPosition = a.getBoolean(R.styleable.MaterialButton_effectPosition, effectPosition);

            } else if (attr == R.styleable.MaterialButton_drawableCenter) {
                drawableCenter = a.getDrawable(R.styleable.MaterialButton_drawableCenter);

            } else if (attr == R.styleable.MaterialButton_drawableLeft) {
                drawableLeft = a.getDrawable(R.styleable.MaterialButton_drawableLeft);

            } else if (attr == R.styleable.MaterialButton_drawableTop) {
                drawableTop = a.getDrawable(R.styleable.MaterialButton_drawableTop);

            } else if (attr == R.styleable.MaterialButton_drawableRight) {
                drawableRight = a.getDrawable(R.styleable.MaterialButton_drawableRight);

            } else if (attr == R.styleable.MaterialButton_drawableBottom) {
                drawableBottom = a.getDrawable(R.styleable.MaterialButton_drawableBottom);

            } else if (attr == R.styleable.MaterialButton_drawablePadding) {
                drawablePadding = a.getInteger(R.styleable.MaterialButton_drawablePadding, getDrawableNull);

            }
        }
        a.recycle();
    }

    //---------------------------------------------------------------------
    // Setup
    //---------------------------------------------------------------------

    protected void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.layout_material_button, this);

            initialiseDaTa();
            initialiseUI();
        }
    }

    protected void initialiseDaTa() {
        this.ANIMATION_DURATION = defDuration;
        this.isActive = false;

        this.alpha = 0.3f;
        this.scaleXFrom = 0.1f;
        this.scaleYFrom = 0.1f;
        this.scaleXTo = defScaleTo;
        this.scaleYTo = defScaleTo;
    }

    protected void initialiseUI() {
        //Don't use padding
        this.setPadding(0, 0, 0, 0);

        //Define
        bgMaterial = findViewById(R.id.bgMaterial);
        btnMaterial = (TextView) findViewById(R.id.btnMaterial);

        //Reset default value
        setDefaultValue();

        //Fill data attributes
        fillDataAttributes();

        //Default ui
        this.bgMaterial.setVisibility(GONE);

        //Event
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerOnClick();
            }
        });
    }

    protected void setDefaultValue() {

        //Background material color
        GradientDrawable bgShape = (GradientDrawable) bgMaterial.getBackground();
        bgShape.setColor(getResources().getColor(R.color.bg_overlay_material_button));
    }

    //---------------------------------------------------------------------
    // Event listener
    //---------------------------------------------------------------------

    public void setText(String text) {
        if (text == null || text.equals("")) {
            btnMaterial.setText("");
            return;
        }

        btnMaterial.setText(text);
    }

    public void setTextSize(float textSize) {
        if (String.valueOf(textSize) == null || String.valueOf(Math.round(textSize)).equals("0") || String.valueOf(textSize).equals("")) {
            return;
        }

        btnMaterial.setTextSize(textSize);
    }

    public void setTextColor(int mColor) {
        if (String.valueOf(mColor) == null || String.valueOf(mColor).equals("")) {
            btnMaterial.setTextColor(Color.BLACK);
            return;
        }

        btnMaterial.setTextColor(mColor);
    }

    public void setEffectColor(Number color) {
        GradientDrawable bgShape = (GradientDrawable) bgMaterial.getBackground();
        bgShape.setColor(color == null ? getResources().getColor(R.color.bg_overlay_material_button) : color.intValue());
    }

    public void setEffectClick(boolean eventClick) {
        this.eventClick = eventClick;
    }

    public void setEffectPosition(boolean effectAble) {
        this.effectPosition = effectAble;
    }

    @SuppressLint("NewApi")
    public void setDrawableCenter(Drawable icon) {
        if (icon == null)
            return;

        btnMaterial.setBackground(icon);
    }

    public void setDrawableLeft(Drawable icon) {
        if (icon == null) {
            setDrawableIcon(null, null, null, null);
            return;
        }

        setDrawableIcon(icon, null, null, null);
    }

    public void setDrawableRight(Drawable icon) {
        if (icon == null) {
            setDrawableIcon(null, null, null, null);
            return;
        }

        setDrawableIcon(null, null, icon, null);
    }

    public void setDrawableTop(Drawable icon) {
        if (icon == null) {
            setDrawableIcon(null, null, null, null);
            return;
        }

        setDrawableIcon(null, icon, null, null);
    }

    public void setDrawableBottom(Drawable icon) {
        if (icon == null) {
            setDrawableIcon(null, null, null, null);
            return;
        }

        setDrawableIcon(null, null, null, icon);
    }

    public void setDrawablePadding(Number padding) {
        if (padding == null) {
            btnMaterial.setCompoundDrawablePadding(0);
            return;
        }

        btnMaterial.setCompoundDrawablePadding(padding.intValue());
    }

    //---------------------------------------------------------------------
    // Private Event
    //---------------------------------------------------------------------

    private void triggerOnClick() {
        //To don't click during animation effecting
        if (isActive == true)
            return;
        materialAnim();

        //Button event clicked during animation effecting
        if (listener == null || eventClick == false)
            return;
        listener.onClick();
    }

    private void resetLikeAnimationState() {
        bgMaterial.setVisibility(GONE);

        this.isActive = false;

        //Button event clicked after animation effected
        if (listener == null || eventClick == true)
            return;
        listener.onClick();
    }

    private void fillDataAttributes() {
        if (text != null) {
            setText(text);
        }
        //Text size
        if (String.valueOf(textSize) != null) {
            setTextSize(textSize);
        }
        //Text color
        if (textColor != null) {
            setTextColor(textColor.intValue());
        }
        //Effect color
        if (effectColor != null) {
            setEffectColor(effectColor);
        }
        //Background color
        if (drawableCenter != null) {
            setDrawableCenter(drawableCenter);
        }
        //Drawable left
        if (drawableLeft != null) {
            setDrawableLeft(drawableLeft);
        }
        //Drawable top
        if (drawableTop != null) {
            setDrawableTop(drawableTop);
        }
        //Drawable right
        if (drawableRight != null) {
            setDrawableRight(drawableRight);
        }
        //Drawable bottom
        if (drawableBottom != null) {
            setDrawableBottom(drawableBottom);
        }
        //Drawable padding
        if (drawablePadding != null) {
            setDrawablePadding(drawablePadding);
        }
    }

    private void setDrawableIcon(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        btnMaterial.setCompoundDrawablesWithIntrinsicBounds(
                left == null ? null : left,
                top == null ? null : top,
                right == null ? null : right,
                bottom == null ? null : bottom
        );
    }

    //---------------------------------------------------------------------
    // Listener
    //---------------------------------------------------------------------

    public void setMaterialButtonClick(MaterialListener listener) {
        this.listener = listener;
    }

    //---------------------------------------------------------------------
    // Animation
    //---------------------------------------------------------------------

    protected void materialAnim() {
        //Don't click button during background effecting
        this.isActive = true;

        //Estimate background effect before start
        setupBackgroundEffect();

        //Show background
        bgMaterial.setVisibility(VISIBLE);

        //Start background
        bgMaterial.setScaleY(scaleYFrom);
        bgMaterial.setScaleX(scaleXFrom);
        bgMaterial.setAlpha(1f);

        //To background
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(bgMaterial, "scaleY", scaleYFrom, scaleYTo);
        bgScaleYAnim.setDuration(ANIMATION_DURATION);
        bgScaleYAnim.setInterpolator(DECELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(bgMaterial, "scaleX", scaleXFrom, scaleXTo);
        bgScaleXAnim.setDuration(ANIMATION_DURATION);
        bgScaleXAnim.setInterpolator(DECELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(bgMaterial, "alpha", 1f, alpha);

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetLikeAnimationState();
            }
        });

        animatorSet.start();
    }

    //---------------------------------------------------------------------
    // Estimate pivot point
    //---------------------------------------------------------------------

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (effectPosition == false) {
            return super.onInterceptTouchEvent(ev);
        }

        if (rootX == 0 || rootY == 0) {
            this.rootX = this.getWidth() / 2;
            this.rootY = this.getHeight() / 2;
        }

        float eventX = ev.getX();
        float eventY = ev.getY();

        float x = rootX - eventX;
        float y = rootY - eventY;

        //phan tu 1
        if (eventX >= rootX && eventY < rootY) {
            x = Utils.getPositive(x);
            y = Utils.getNegative(y);
        }
        //phan tu 2
        else if (eventX > rootX && eventY >= rootY) {
            x = Utils.getPositive(x);
            y = Utils.getPositive(y);
        }
        //phan tu 3
        else if (eventX <= rootX && eventY > rootY) {
            x = Utils.getNegative(x);
            y = Utils.getPositive(y);
        }
        //phan tu 4
        else if (eventX < rootX && eventY < rootY) {
            x = Utils.getNegative(x);
            y = Utils.getNegative(y);
        }

        //translate to new rootX and rootY
        if (bgMaterial != null) {
            bgMaterial.setTranslationX(x);
            bgMaterial.setTranslationY(y);
        }

        //estimate with new width new height
        int newWidth = (eventX > rootX ? Math.round(eventX) : Math.round(this.getWidth() - eventX)) * 2;
        int newHeight = (eventY > rootY ? Math.round(eventY) : Math.round(this.getHeight() - eventY)) * 2;
        estimate_Scale_To(newWidth, newHeight);

        return super.onInterceptTouchEvent(ev);
    }

    //---------------------------------------------------------------------
    // Prepare before animation effect background
    //---------------------------------------------------------------------

    protected void setupBackgroundEffect() {
        int width = this.getWidth();
        int height = this.getHeight();

        calculate_With_And_Height(width, height);

        estimate_Scale_Start(width, height);

        if (effectPosition == false) {
            estimate_Scale_To(width, height);
        }

        handle_Duration();
    }

    protected void calculate_With_And_Height(int width, int height) {
        LayoutParams params = null;

        if (width == height) {
            params = new LayoutParams(width, height);
        }

        if (width > height) {
            params = new LayoutParams(height, height);
        }

        if (width < height) {
            params = new LayoutParams(width, width);
        }

        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        bgMaterial.setLayoutParams(params);
    }

    protected void estimate_Scale_To(int width, int height) {
        if (width == height) {
            this.scaleYTo = defScaleTo;
            this.scaleXTo = defScaleTo;
        }
        else if (width > height) {
            this.scaleYTo = (width * defScaleTo) / height;
            this.scaleXTo = (width * defScaleTo) / height;
        }
        else if (height > width){
            this.scaleYTo = (height * defScaleTo) / width;
            this.scaleXTo = (height * defScaleTo) / width;
        }
    }

    protected void estimate_Scale_Start(int width, int height) {
        if (effectPosition == true) {
            this.scaleXFrom = startScaleWithEffectPosition;
            this.scaleYFrom = startScaleWithEffectPosition;
            return;
        }

        if (width == height) {
            this.scaleXFrom = defScaleFrom;
            this.scaleYFrom = defScaleFrom;
            return;
        }

        //Button not square
        this.scaleXFrom = rectangle;
        this.scaleYFrom = rectangle;

        //Button too long
        int distance = (width > height) ? width / height : height / width;
        if (distance > 2) {
            this.scaleXFrom = longRectangle;
            this.scaleYFrom = longRectangle;
        }
    }

    protected void handle_Duration() {
        if (this.scaleYTo == defScaleTo) {
            ANIMATION_DURATION = defDuration;
            return;
        }

        if (mContext == null) {
            ANIMATION_DURATION = 300;
            return;
        }

        float screenWith = Utils.getScreenWidth(mContext);
        float distance = screenWith / this.getWidth();
        if (distance <= 2) {
            ANIMATION_DURATION = 300;
        }

        float screenHeight = Utils.getScreenHeight(mContext);
        distance = screenHeight / this.getHeight();
        if (distance <= 3) {
            ANIMATION_DURATION = 300;
        }
    }
}

