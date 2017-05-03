package anvenkat.calpoly.edu.studybuddiesv02;

import android.content.Context;
import android.support.v4.app.Fragment;

/* Base fragment to ensure the parent activity implements a contract interface. */
public abstract class ContractFragment<T> extends Fragment {
    private T mContract;

    @Override
    public void onAttach(Context context) {
        try {
            mContract = (T)context;
        } catch (ClassCastException e) {
            throw new IllegalStateException(context.getClass().getSimpleName()
                    + " does not implement " + getClass().getSimpleName() + "'s contract interface.", e);
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContract = null;
    }

    public final T getContract() {
        return mContract;
    }
}