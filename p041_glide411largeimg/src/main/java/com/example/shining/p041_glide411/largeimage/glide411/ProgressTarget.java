package com.example.shining.p041_glide411.largeimage.glide411;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.shining.p041_glide411.application.DemoApplication;

public abstract class ProgressTarget<T, Z> extends WrappingTarget<Z> implements OkHttpProgressGlideModule.UIProgressListener {
    private T model;
    private boolean ignoreProgress = true;

    public ProgressTarget(T model, Target<Z> target) {
        super(target);
        this.model = model;
    }

    public final T getModel() {
        return model;
    }

    public final void setModel(T model) {
//        Glide.clear(this); // indirectly calls cleanup
        Glide.with(DemoApplication.mContext).clear(this);
        this.model = model;
    }

    /**
     * Convert a model into an Url string that is used to match up the OkHttp requests. For explicit
     * {@link com.bumptech.glide.load.model.GlideUrl GlideUrl} loads this needs to return
     * {@link com.bumptech.glide.load.model.GlideUrl#toStringUrl toStringUrl}. For custom models do the same as your
     * {@link com.bumptech.glide.load.model.stream.BaseGlideUrlLoader BaseGlideUrlLoader} does.
     *
     * @param model return the representation of the given model, DO NOT use {@link #getModel()} inside this method.
     * @return a stable Url representation of the model, otherwise the progress reporting won't work
     */
    protected String toUrlString(T model) {
        return String.valueOf(model);
    }

    @Override
    public float getGranualityPercentage() {
        return 1.0f;
    }

    private void start() {
        OkHttpProgressGlideModule.expect(toUrlString(model), this);
        ignoreProgress = false;
    }

    private void cleanup() {
        ignoreProgress = true;
        T model = this.model; // save in case it gets modified
        OkHttpProgressGlideModule.forget(toUrlString(model));
        this.model = null;
    }

    @Override
    public void onLoadStarted(android.graphics.drawable.Drawable placeholder) {
        super.onLoadStarted(placeholder);
        start();
    }

    @Override
    public void onResourceReady(Z resource, Transition<? super Z> animation) {
        cleanup();
        super.onResourceReady(resource, animation);
    }

    @Override
    public void onLoadFailed(android.graphics.drawable.Drawable errorDrawable) {
        cleanup();
        super.onLoadFailed(errorDrawable);
    }

    @Override
    public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {
        cleanup();
        super.onLoadCleared(placeholder);
    }
}