package aaron.collapsingtoolbarlayout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener{

    private ListView listView;
    private Toolbar toolbar;
    private TextView floatTitle;
    private ImageView headerBg;

    private float headerHeight;//顶部高度
    private float minHeaderHeight;//顶部最低高度，即Bar的高度
    private float floatTitleLeftMargin;//header标题文字左偏移量
    private float floatTitleSize;//header标题文字大小
    private float floatTitleSizeLarge;//header标题文字大小（大号）

    private void initMeasure(){
        headerHeight = getResources().getDimension(R.dimen.header_height);
        minHeaderHeight = getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
        floatTitleLeftMargin = getResources().getDimension(R.dimen.float_title_left_margin);
        floatTitleSize = getResources().getDimension(R.dimen.float_title_size);
        floatTitleSizeLarge = getResources().getDimension(R.dimen.float_title_size_large);
    }
    private void initView() {
        listView = (ListView) findViewById(R.id.lv_main);
        floatTitle = (TextView) findViewById(R.id.tv_main_title);
        toolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListView() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            data.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.activity_list_item, android.R.id.text1, data);
        listView.setAdapter(adapter);
    }

    private void initListViewHeader() {
        View headerContainer = LayoutInflater.from(this).inflate(R.layout.header, listView, false);
        headerBg = (ImageView) headerContainer.findViewById(R.id.img_header_bg);

        listView.addHeaderView(headerContainer);
    }

    private void initEvent() {
        listView.setOnScrollListener(this);
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        //y轴偏移量
        float scrollY = getScrollY(view);

        //变化率
        float headerBarOffsetY = headerHeight - minHeaderHeight;
        float offset =  1 - Math.max((headerBarOffsetY - scrollY) / headerBarOffsetY, 0f);

        //Toolbar背景色透明度
        toolbar.setBackgroundColor(Color.argb((int) (offset * 255), 0, 0, 0));

        //header背景图Y轴偏移  让背景图出现变动效果，偏移量为1/2 保证图片不会跟listview 分离
        headerBg.setTranslationY(scrollY / 2);
        /*** 标题文字处理 ***/
        //标题文字缩放圆心（X轴）
       floatTitle.setPivotX(floatTitle.getLeft() + floatTitle.getPaddingLeft());
        //标题文字缩放比例
        float titleScale = floatTitleSize / floatTitleSizeLarge;
        floatTitle.setTranslationX(floatTitleLeftMargin * offset);
        //标题文字Y轴偏移：（-缩放高度差 + 大文字与小文字高度差）/ 2 * 变化率 + Y轴滑动偏移
        floatTitle.setTranslationY(
                (-(floatTitle.getHeight() - minHeaderHeight) +//-缩放高度差
                        floatTitle.getHeight() * (1 - titleScale))//大文字与小文字高度差
                        / 2 * offset +
                        (headerHeight - floatTitle.getHeight()) * (1 - offset));//Y轴滑动偏移

        //标题文字X轴缩放
        floatTitle.setScaleX(1 - offset * (1 - titleScale));
        //标题文字Y轴缩放
        floatTitle.setScaleY(1 - offset * (1 - titleScale));


        //去掉标题文字的显示
        toolbar.setTitle("");
    }

    public  float getScrollY(AbsListView view){
        //计算滚动了多长位置
        View c = view.getChildAt(0);  //获取第一位置的item
        if(c == null){
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition(); //可视界面里第一个item 的position
        int top  = c.getTop();  //可视界面里第一个item 高度，滚动出界面的是负数
        float headerHeight = 0 ;
        if(firstVisiblePosition >=1){  //hearview 占据第0位置
            headerHeight = this.headerHeight;
        }
        return -top+firstVisiblePosition*c.getHeight()+headerHeight;
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMeasure();
        initView();
        initListViewHeader();
        initListView();
        initEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
