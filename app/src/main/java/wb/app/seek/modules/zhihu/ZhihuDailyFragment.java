package wb.app.seek.modules.zhihu;

import android.animation.Animator;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import wb.app.seek.R;
import wb.app.seek.common.base.mvp.MvpFragment;
import wb.app.seek.common.utils.DateTimeUtils;
import wb.app.seek.modules.model.ZhihuDailyNews;
import wb.app.seek.modules.model.ZhihuDailyStory;
import wb.app.seek.modules.zhihu.adapter.ZhihuDailyAdapter;
import wb.app.seek.modules.zhihu.presenter.ZhihuDailyContract;
import wb.app.seek.modules.zhihu.presenter.ZhihuDailyPresenter;
import wb.app.seek.widgets.recyclerview.OnRecyclerViewScrollListener;

import static wb.app.seek.modules.zhihu.ZhihuDailyDetailActivity.INTENT_KEY_STORY_ID;

/**
 * Created by W.b on 2017/2/9.
 */
public class ZhihuDailyFragment extends MvpFragment<ZhihuDailyPresenter> implements ZhihuDailyContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.rocket_fab) FloatingActionButton mRocketFab;
    private ZhihuDailyAdapter mZhihuListAdapter;
//    private Subscription mSubscription;

    public static ZhihuDailyFragment newInstance() {
        return new ZhihuDailyFragment();
    }

    @Override
    protected ZhihuDailyPresenter createPresenter() {
        return new ZhihuDailyPresenter();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_zhihu;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (!mSubscription.isUnsubscribed()) {
//            mSubscription.unsubscribe();
//        }
    }

    @Override
    protected void initComponents(View view) {
        initRecyclerView();

        getPresenter().queryLatest();

        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });

//        mSubscription = RxBus.getInstance().toObservable(RxEvent.class)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<RxEvent>() {
//                    @Override
//                    public void call(RxEvent rxEvent) {
//                        if (rxEvent.getType() == RxEventType.SCROLL_TO_TOP) {
//                            smoothScrollTop();
//                            getPresenter().refreshNews(rxEvent.getMessage());
//                        }
//                    }
//                });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mZhihuListAdapter = new ZhihuDailyAdapter();
        mZhihuListAdapter.setOnItemClickListener(new ZhihuDailyAdapter.OnItemClickListener() {

            @Override
            public void onClick(int id, View view) {
                // Lollipop 以上实现 Transition
                Intent intent = new Intent(getActivity(), ZhihuDailyDetailActivity.class);
                intent.putExtra(INTENT_KEY_STORY_ID, id);
                ActivityOptionsCompat options
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, getString(R.string.transition_name_cover_image));
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                getActivity().overridePendingTransition(0, 0);
            }

            @Override
            public void onBannerClick(int id) {
                Intent intent = new Intent(getActivity(), ZhihuDailyDetailActivity.class);
                intent.putExtra(INTENT_KEY_STORY_ID, id);
                getActivity().startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mZhihuListAdapter);
        mRecyclerView.addOnScrollListener(onScrollListener);
    }

    private RecyclerView.OnScrollListener onScrollListener = new OnRecyclerViewScrollListener() {

        @Override
        protected void onRefresh(boolean isCanRefresh) {
            mRefreshLayout.setEnabled(isCanRefresh ? true : false);
        }

        @Override
        protected void onLoadMore() {
            getPresenter().loadMoreNews();
        }

        @Override
        public void showRocket() {
            super.showRocket();

            mRocketFab.show();
        }

        @Override
        public void hideRocket() {
            super.hideRocket();

            mRocketFab.hide();
        }
    };

    @Override
    public void showLoading() {
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
    }

    @Override
    public void showNoMore() {
        mZhihuListAdapter.showNoMoreNews();
    }

    @Override
    public void showNews(ZhihuDailyNews dailyNews) {
        mZhihuListAdapter.showNews(dailyNews);
    }

    @Override
    public void showTopStory(List<ZhihuDailyStory> dailyStoryList) {
        mZhihuListAdapter.showTopStory(dailyStoryList);
    }

    @Override
    public void showMoreNews(ZhihuDailyNews dailyNews) {
        mZhihuListAdapter.showMoreNews(dailyNews);
    }

    @Override
    public void onRefresh() {
        getPresenter().refreshNews(DateTimeUtils.getCurrentDay());
    }

    @OnClick({R.id.rocket_fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rocket_fab:
                // 快速滚到到顶部
                smoothScrollTop();
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(mRocketFab, 200, 200, 0, 50);

                anim.start();
                break;
        }
    }

    private void smoothScrollTop() {
        mRecyclerView.scrollToPosition(0);
//    mRecyclerView.smoothScrollToPosition(0);
    }
}
