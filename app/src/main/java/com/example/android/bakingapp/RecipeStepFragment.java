package com.example.android.bakingapp;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class RecipeStepFragment extends Fragment {

    public static final String ARG_RECIPE = "recipe";
    public static final String ARG_RECIPE_STEP_INDEX = "recipe_step_index";

    private Recipe mRecipe;
    private int mStepIndex;
    private boolean mTwoPane;

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;

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
                        releasePlayer();
                        setStepViews();                }
                }
            });

            mNextStepImageView = rootView.findViewById(R.id.next_step_iv);
            mNextStepImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mStepIndex < mRecipe.getSteps().size() - 1) {
                        mStepIndex++;
                        releasePlayer();
                        setStepViews();
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(ARG_RECIPE_STEP_INDEX, mStepIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void setStepViews() {
        Step recipeStep = mRecipe.getSteps().get(mStepIndex);
        if (!TextUtils.isEmpty(recipeStep.getVideoURL())) {
            initializePlayer(Uri.parse(recipeStep.getVideoURL()));
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

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            String userAgent = Util.getUserAgent(getContext(), "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
            mPlayerView.hideController();
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

}