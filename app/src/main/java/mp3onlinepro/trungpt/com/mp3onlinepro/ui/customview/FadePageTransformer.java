package mp3onlinepro.trungpt.com.mp3onlinepro.ui.customview;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Set this PageTransformer on a ViewPager. It will be invoked anytime it's pages are scrolled.
 * This transformer has the effect of fading in and out pages on top of each other.
 */
public class FadePageTransformer implements ViewPager.PageTransformer {

    @Override
    public final void transformPage(final View view, final float position) {
        final int pageWidth = view.getWidth();

        /*
         * When a page's alpha is set to 0 it's visibility should also be set to gone.
         * Even though the view isn't visible it can still be interacted with if it isn't gone and is drawn on top.
         */

        /*
         * Position is checked right up next to -1 and 1. The reason is because sometimes the position doesn't seem to come
         * all the way through as a whole number. Meaning it seems it would stop so very close to -1 or 0 (for example) and
         * the code to make necessary views 'gone' never gets called. So then there could be an invisible view on top that is
         * still able to be interacted with.
         */

        if (position < -0.999f) { // [-Infinity,-1)
            // This page is way off-screen to the left so hide it.
            view.setAlpha(0);
            view.setVisibility(View.GONE);
            view.setTranslationX(pageWidth);
        } else if (position <= 0.999f) { // (-1, 1)
            // The further the page is from being center page the more transparent it is.
            view.setAlpha(getAlpha(position));
            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);
            // Make sure the page is visible
            view.setVisibility(View.VISIBLE);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right so hide it.
            view.setAlpha(0);
            view.setVisibility(View.GONE);
            view.setTranslationX(-pageWidth);
        }
    }

    /**
     * Get the alpha value that should be applied to a position.
     *
     * @param position Position to find an alpha for.
     * @return An alpha value.
     */
    private static final float getAlpha(final float position) {
        return getSlowQuadraticAlpha(position);
    }

    private static final float getLinearAlpha(final float position) {
        if (position <= 0) {
            return 1 + position;
        }
        return 1 - position;
    }

    private static final float getFastQuadraticAlpha(final float position) {
        final float linearAlpha = getLinearAlpha(position);
        return linearAlpha * linearAlpha;
    }

    private static final float getSlowQuadraticAlpha(final float position) {
        return 1 - position * position;
    }
}