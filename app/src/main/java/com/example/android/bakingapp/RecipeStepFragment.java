package com.example.android.bakingapp;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.model.Step;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class RecipeStepFragment extends Fragment {

    private static final String TAG = "RecipeStepFragment";

    public static final String ARG_RECIPE = "recipe";
    public static final String ARG_RECIPE_STEP_INDEX = "recipe_step_index";
    public static final String BUNDLE_PLAYER_POSITION = "player_position";
    public static final String BUNDLE_PLAYER_STATE = "player_state";

    private Recipe mRecipe;
    private int mStepIndex;
    private boolean mTwoPane;

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private long mPlayerPosition;
    private boolean mPlayerState;

    private TextView mDescriptionTextView;
    private ImageView mNoVideoImageView;
    private TextView mNoVideoTextView;
    private ImageView mPreviousStepImageView;
    private ImageView mNextStepImageView;

    public RecipeStepFragment() {
        // Required empty public constructor
    }

    public static RecipeStepFragment newInstance(Recipe recipe, int stepIndex) {
        RecipeStepFragment fragment = new RecipeStepFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        args.putInt(ARG_RECIPE_STEP_INDEX, stepIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.tag(TAG).d("onCreate: starts");

        if (savedInstanceState != null) {
            mPlayerPosition = savedInstanceState.getLong(BUNDLE_PLAYER_POSITION, C.TIME_UNSET);
            mPlayerState = savedInstanceState.getBoolean(BUNDLE_PLAYER_STATE, true);
        } else {
            mPlayerState = true;
            mPlayerPosition = C.TIME_UNSET;
        }
        Timber.tag(TAG).d("onCreate: player position=%d", mPlayerPosition);

        if (getArguments() != null) {
            mRecipe = getArguments().getParcelable(ARG_RECIPE);
            if (savedInstanceState == null) {
                mStepIndex = getArguments().getInt(ARG_RECIPE_STEP_INDEX);
            } else {
                mStepIndex = savedInstanceState.getInt(ARG_RECIPE_STEP_INDEX);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Timber.tag(TAG).d("onCreateView: starts");

        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        mDescriptionTextView = rootView.findViewById(R.id.recipe_description_tv);
        mDescriptionTextView.setMovementMethod(new ScrollingMovementMethod());

        mPlayerView = rootView.findViewById(R.id.playerView);
        mPlayerView.setDefaultArtwork(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_left_on, null));

        mNoVideoImageView = rootView.findViewById(R.id.no_video_iv);
        mNoVideoTextView = rootView.findViewById(R.id.no_video_tv);

        LinearLayout stepNavigationView = rootView.findViewById(R.id.step_navigation_ll);

        if (stepNavigationView == null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;

            mPreviousStepImageView = rootView.findViewById(R.id.previous_step_iv);
            mPreviousStepImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mStepIndex > 0) {
                        mStepIndex--;
                        changeStep();
                    }
                }
            });

            mNextStepImageView = rootView.findViewById(R.id.next_step_iv);
            mNextStepImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mStepIndex < mRecipe.getSteps().size() - 1) {
                        mStepIndex++;
                        changeStep();
                    }
                }
            });
        }

        setStepViews();

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(mRecipe.getName());
                actionBar.setSubtitle(getString(R.string.step_label));
            }
        }

        return rootView;
    }

    private void changeStep() {
        releasePlayer();
        mPlayerPosition = C.TIME_UNSET;
        mPlayerState = true;
        initializePlayer();
        setStepViews();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Timber.tag(TAG).d("onSaveInstanceState: starts");
        outState.putInt(ARG_RECIPE_STEP_INDEX, mStepIndex);
        if (mExoPlayer != null) {
            Timber.tag(TAG).d("onSaveInstanceState: Active player position=%d ", mExoPlayer.getCurrentPosition());
            outState.putLong(BUNDLE_PLAYER_POSITION, mExoPlayer.getCurrentPosition());
            outState.putBoolean(BUNDLE_PLAYER_STATE, mExoPlayer.getPlayWhenReady());
        } else {
            Timber.tag(TAG).d("onSaveInstanceState: Stored player position=%d", mPlayerPosition);
            outState.putLong(BUNDLE_PLAYER_POSITION, mPlayerPosition);
            outState.putBoolean(BUNDLE_PLAYER_STATE, mPlayerState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Timber.tag(TAG).d("onStart: starts");

        if (Util.SDK_INT > 23) {
            initializePlayer();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        Timber.tag(TAG).d("onResume: starts");

        if ((Util.SDK_INT <= 23 || mExoPlayer == null)) {
            initializePlayer();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        Timber.tag(TAG).d("onPause: starts");

        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        Timber.tag(TAG).d("onStop: starts");

        if (Util.SDK_INT > 23) {
            releasePlayer();
        }

    }

    private void setStepViews() {
        Step recipeStep = mRecipe.getSteps().get(mStepIndex);
        if (!TextUtils.isEmpty(recipeStep.getVideoURL())) {
            mNoVideoImageView.setVisibility(View.INVISIBLE);
            mNoVideoTextView.setVisibility(View.INVISIBLE);
        } else {
            mPlayerView.setPlayer(null);
            mNoVideoImageView.setVisibility(View.VISIBLE);
            mNoVideoTextView.setVisibility(View.VISIBLE);
        }
        mDescriptionTextView.setText(recipeStep.getDescription());
        if (!mTwoPane) {
            if (mStepIndex == 0) {
                mPreviousStepImageView.setVisibility(View.INVISIBLE);
                mNextStepImageView.setVisibility(View.VISIBLE);
            } else {
                mPreviousStepImageView.setVisibility(View.VISIBLE);
                if (mStepIndex == mRecipe.getSteps().size() - 1) {
                    mNextStepImageView.setVisibility(View.INVISIBLE);
                } else {
                    mNextStepImageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void initializePlayer() {

        Timber.tag(TAG).d("initializePlayer: starts");

        Step recipeStep = mRecipe.getSteps().get(mStepIndex);
        if ( (mExoPlayer == null) && !TextUtils.isEmpty(recipeStep.getVideoURL()) ) {
            Uri mediaUri = Uri.parse(recipeStep.getVideoURL());
            Timber.tag(TAG).d("initializePlayer: uri=%s", mediaUri.toString());
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            Timber.tag(TAG).d("initializePlayer: player position=%d", mPlayerPosition);
            mExoPlayer.prepare(mediaSource);
            if (mPlayerPosition != C.TIME_UNSET) {
                Timber.tag(TAG).d("initializePlayer: seekTo=%d", mPlayerPosition);
                mExoPlayer.seekTo(mPlayerPosition);
            }
            Timber.tag(TAG).d("initializePlayer: player mPlayerState=%b", mPlayerState);
            mExoPlayer.setPlayWhenReady(mPlayerState);
            mPlayerView.hideController();
        }
    }

    private void releasePlayer() {

        Timber.tag(TAG).d("releasePlayer: starts");

        if (mExoPlayer != null) {
            Timber.tag(TAG).d("releasePlayer: stop and release");
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            mPlayerState = mExoPlayer.getPlayWhenReady();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

}