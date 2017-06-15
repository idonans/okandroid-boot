package com.okandroid.boot.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.okandroid.boot.util.ViewUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by idonans on 2015/12/28.
 * 数据分组的RecyclerView Adapter
 */
public class RecyclerViewGroupAdapter extends RecyclerView.Adapter {

    private final SparseArrayCompat mData;
    private final RecyclerView mRecyclerView;

    public RecyclerViewGroupAdapter(RecyclerView recyclerView) {
        this(new SparseArrayCompat(5), recyclerView);
    }

    public RecyclerViewGroupAdapter(SparseArrayCompat data, RecyclerView recyclerView) {
        mData = data;
        mRecyclerView = recyclerView;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public SparseArrayCompat getData() {
        return mData;
    }

    /**
     * {@inheritDoc}
     * 默认实现 viewType 与 group 一致
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EmptyViewHolder(this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HolderUpdate) {
            ((HolderUpdate) holder).onHolderUpdate(getItem(position), position);
        }
    }

    /**
     * 获得指定组下的数据数量，如果该组下没有数据，返回0．
     */
    public int getGroupItemCount(int group) {
        Object object = mData.get(group);
        if (object == null) {
            return 0;
        }

        return ((ArrayList) object).size();
    }

    /**
     * <pre>
     * 清除某一组数据，如果该组下没有数据，返回 <code>null</code>.
     * 否则返回一个长度为 2 的整数数组，其中
     * [0] 标识被清除数据在整体数据中的开始位置，总是 <code>>=0</code>
     * [1] 标识被清除的数据的长度(即清除前该组数据的数量)，总是 <code>>0</code>
     *
     * 使用示例：
     * <code>
     *
     * int[] positionAndSize = mAdapter.clearGroupItems(GROUP_DATA);
     * if(positionAndSize != null) {
     *     mAdapter.notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
     * }
     *
     * </code>
     * </pre>
     */
    public int[] clearGroupItems(int group) {
        Object object = mData.get(group);
        if (object == null) {
            return null;
        }

        ArrayList groupItems = (ArrayList) object;
        if (groupItems.isEmpty()) {
            return null;
        }

        int[] result = new int[2];
        result[0] = getGroupPositionStart(group);
        result[1] = groupItems.size();

        groupItems.clear();

        return result;
    }

    /**
     * 获取指定组在全局所在的开始位置, 总是 <code>>=0</code>
     */
    public int getGroupPositionStart(int group) {
        int position = 0;

        int size = mData.size();
        for (int i = 0; i < size; i++) {
            int groupNum = mData.keyAt(i);
            if (groupNum >= group) {
                break;
            } else {
                Object groupItems = mData.valueAt(i);
                if (groupItems != null) {
                    position += ((ArrayList) groupItems).size();
                }
            }
        }

        return position;
    }

    /**
     * <pre>
     * 清除指定组下指定位置的数据，如果该数据没有找到，返回 <code>null</code>.
     * 否则返回一个长度为 2 的整数数组，其中
     * [0] 标识被清除数据在整体数据中的开始位置，总是 <code>>=0</code>
     * [1] 标识被清除的数据的长度，总是 <code>=1</code>
     *
     * 使用示例：
     * <code>
     *
     * int[] positionAndSize = mAdapter.removeGroupItem(GROUP_DATA, 3);
     * if(positionAndSize != null) {
     *     mAdapter.notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
     * }
     *
     * </code>
     * </pre>
     */
    public int[] removeGroupItem(int group, int positionInGroup) {
        if (positionInGroup < 0) {
            return null;
        }

        Object object = mData.get(group);
        if (object == null) {
            return null;
        }

        ArrayList groupItems = (ArrayList) object;
        if (groupItems.size() <= positionInGroup) {
            return null;
        }

        groupItems.remove(positionInGroup);

        int[] result = new int[2];
        result[0] = getGroupPositionStart(group) + positionInGroup;
        result[1] = 1;
        return result;
    }

    /**
     * <pre>
     * 清除指定组下指定位置区域的数据，如果该区域不合法，返回 <code>null</code>.
     * 否则返回一个长度为 2 的整数数组，其中
     * [0] 标识被清除数据在整体数据中的开始位置，总是 <code>>=0</code>
     * [1] 标识被清除的数据的长度，总是 <code>>0</code>
     *
     * 使用示例：
     * <code>
     *
     * int[] positionAndSize = mAdapter.removeGroupItems(GROUP_DATA, 3, 2); // 删除该组数据的第3项和第4项
     * if(positionAndSize != null) {
     *     mAdapter.notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
     * }
     *
     * </code>
     * </pre>
     */
    public int[] removeGroupItems(int group, int positionInGroup, int size) {
        if (positionInGroup < 0) {
            return null;
        }

        if (size <= 0) {
            return null;
        }

        Object object = mData.get(group);
        if (object == null) {
            return null;
        }

        ArrayList groupItems = (ArrayList) object;
        if (groupItems.size() < positionInGroup + size) {
            return null;
        }

        ((ArrayListWrapper) groupItems).removeRangeWrapper(positionInGroup, size);

        int[] result = new int[2];
        result[0] = getGroupPositionStart(group) + positionInGroup;
        result[1] = size;
        return result;
    }

    private static final class ArrayListWrapper extends ArrayList {

        public ArrayListWrapper(int capacity) {
            super(capacity);
        }

        public ArrayListWrapper() {
        }

        public ArrayListWrapper(Collection collection) {
            super(collection);
        }

        private void removeRangeWrapper(int fromIndex, int size) {
            removeRange(fromIndex, fromIndex + size);
        }

    }

    /**
     * <pre>
     * 获取指定位置所在的组以及组内的位置，如果该位置没有找到，返回 <code>null</code>.
     * 否则返回一个长度为 2 的整数数组，其中
     * [0] 标识所在的组，总是 <code>>=0</code>
     * [1] 标识在该组内所处的位置，总是 <code>>=0</code>
     *
     * 使用示例：
     * <code>
     *
     * int[] groupAndPosition = mAdapter.getGroupAndPosition(13);
     * if(groupAndPosition != null) {
     *     int group = groupAndPosition[0];
     *     int positionInGroup = groupAndPosition[1];
     * } else {
     *     // item not found
     * }
     *
     * </code>
     * </pre>
     */
    public int[] getGroupAndPosition(int position) {
        if (position < 0) {
            return null;
        }

        int[] groupAndPosition = new int[2];
        int globalPosition = 0;

        int size = mData.size();
        for (int i = 0; i < size; i++) {
            int groupItemCount = 0;
            Object groupItems = mData.valueAt(i);
            if (groupItems != null) {
                groupItemCount = ((ArrayList) groupItems).size();
            }
            if (position < globalPosition + groupItemCount) {
                // position在第i组内的(position-globalPosition)位置
                groupAndPosition[0] = mData.keyAt(i);
                groupAndPosition[1] = position - globalPosition;
                return groupAndPosition;
            }
            globalPosition += groupItemCount;
        }

        return null;
    }

    /**
     * <pre>
     * 清除指定位置的数据，如果该数据没有找到，返回 <code>null</code>.
     * 否则返回一个长度为 2 的整数数组，其中
     * [0] 标识被清除数据在整体数据中的开始位置, 总是 <code>=position</code> (传入的参数)
     * [1] 标识被清除的数据的长度，总是 <code>=1</code>
     *
     * 使用示例：
     * <code>
     *
     * int[] positionAndSize = mAdapter.removeItem(13);
     * if(positionAndSize != null) {
     *     mAdapter.notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
     * }
     *
     * </code>
     * </pre>
     */
    public int[] removeItem(int position) {
        int[] groupAndPosition = getGroupAndPosition(position);
        if (groupAndPosition == null) {
            return null;
        }

        return removeGroupItem(groupAndPosition[0], groupAndPosition[1]);
    }

    /**
     * <pre>
     * 清除指定位置附近的数据，如果没有数据可以匹配，返回 <code>null</code>.
     * 否则返回一个长度为 2 的整数数组，其中
     * [0] 标识被清除数据在整体数据中的开始位置, 总是 <code>>=0</code>
     * [1] 标识被清除的数据的长度，总是 <code>>0</code>
     *
     * Filter 用来匹配需要删除的数据，所删除的数据总是在同一组并且相邻
     *
     * 使用示例：
     * <code>
     *
     * int[] positionAndSize = mAdapter.removeItem(13, filter);
     * if(positionAndSize != null) {
     *     mAdapter.notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
     * }
     *
     * </code>
     * </pre>
     */
    public int[] removeItems(int position, Filter filter) {
        int[] groupAndPosition = getGroupAndPosition(position);
        if (groupAndPosition == null) {
            return null;
        }

        int groupSize = getGroupItemCount(groupAndPosition[0]);
        // 根据position确定删除的区域

        // 搜寻开始位置
        int start = -1;
        for (int i = groupAndPosition[1]; i >= 0; i--) {
            Object item = getGroupItem(groupAndPosition[0], i);
            if (!filter.filter(item)) {
                break;
            }
            start = i;
        }

        if (start < 0) {
            return null;
        }

        int end = groupAndPosition[1];
        for (int i = end + 1; i < groupSize; i++) {
            Object item = getGroupItem(groupAndPosition[0], i);
            if (!filter.filter(item)) {
                break;
            }
            end = i;
        }

        // 删除[start, end]区间的数据
        return removeGroupItems(groupAndPosition[0], start, end - start + 1);
    }

    public interface Filter {
        boolean filter(Object item);
    }

    /**
     * <pre>
     * 将数据从一个位置移动到另一个位置, 如果移动失败，返回 <code>null</code>.
     * 否则返回一个长度为 2 的整数数组，其中
     * [0] 标识移动前的位置
     * [1] 标识移动后的位置
     * 不同的ViewType之间不能移动
     *
     * 使用示例：
     * <code>
     *
     * int[] movePosition = mAdapter.move(fromPosition, toPosition);
     * if (movePosition != null) {
     *     mAdapter.notifyItemMoved(movePosition[0], movePosition[1]);
     * }
     *
     * </code>
     * </pre>
     */
    public int[] move(int fromPosition, int toPosition) {
        if (fromPosition < 0
                || toPosition < 0
                || fromPosition == toPosition) {
            return null;
        }

        int[] groupAndPositionFrom = getGroupAndPosition(fromPosition);
        int[] groupAndPositionTo = getGroupAndPosition(toPosition);

        if (groupAndPositionFrom == null
                || groupAndPositionTo == null) {
            return null;
        }

        int itemViewTypeFrom = getGroupItemViewType(fromPosition, groupAndPositionFrom[0], groupAndPositionFrom[1]);
        int itemViewTypeTo = getGroupItemViewType(toPosition, groupAndPositionTo[0], groupAndPositionTo[1]);

        if (itemViewTypeFrom == itemViewTypeTo) {
            // 类型相同，可以直接移动
            if (groupAndPositionFrom[0] == groupAndPositionTo[0]) {
                // 同组内移动
                ArrayList groupItems = (ArrayList) mData.get(groupAndPositionFrom[0]);
                Object object = groupItems.remove(groupAndPositionFrom[1]);
                groupItems.add(groupAndPositionTo[1], object);
            } else {
                // 不同组之间移动
                ArrayList groupItemsFrom = (ArrayList) mData.get(groupAndPositionFrom[0]);
                ArrayList groupItemsTo = (ArrayList) mData.get(groupAndPositionTo[0]);
                Object object = groupItemsFrom.remove(groupAndPositionFrom[1]);

                if (fromPosition > toPosition) {
                    groupItemsTo.add(groupAndPositionTo[1], object);
                } else {
                    groupItemsTo.add(groupAndPositionTo[1] + 1, object);
                }
            }
            return new int[]{fromPosition, toPosition};
        }

        /*
         * 从from到to的方向，to后面的一个位置. 当前位置不能移动，但是当前位置的下一个位置可以移动时，仍然要处理移动。
         * 此时应当移动到下一个位置前面(向移动前的位置的方向)。
         */
        int positionToNext;
        int[] groupAndPositionToNext;
        if (fromPosition < toPosition) {
            positionToNext = toPosition + 1;
        } else {
            positionToNext = toPosition - 1;
        }
        groupAndPositionToNext = getGroupAndPosition(positionToNext);
        if (groupAndPositionToNext == null) {
            // 没有可移动的下一个位置
            return null;
        }

        // try move to before positionToNext
        int itemViewTypeToNext = getGroupItemViewType(positionToNext, groupAndPositionToNext[0], groupAndPositionToNext[1]);

        if (itemViewTypeFrom == itemViewTypeToNext) {
            // 类型相同，可以移动
            if (groupAndPositionFrom[0] == groupAndPositionToNext[0]) {
                // 同组内移动
                ArrayList groupItems = (ArrayList) mData.get(groupAndPositionFrom[0]);
                Object object = groupItems.remove(groupAndPositionFrom[1]);
                groupItems.add(groupAndPositionToNext[1], object);
            } else {
                // 不同组之间移动
                ArrayList groupItemsFrom = (ArrayList) mData.get(groupAndPositionFrom[0]);
                ArrayList groupItemsToNext = (ArrayList) mData.get(groupAndPositionToNext[0]);
                Object object = groupItemsFrom.remove(groupAndPositionFrom[1]);
                if (fromPosition > toPosition) {
                    groupItemsToNext.add(groupAndPositionToNext[1] + 1, object);
                } else {
                    groupItemsToNext.add(groupAndPositionToNext[1], object);
                }
            }
            return new int[]{fromPosition, toPosition};
        }

        return null;
    }

    /**
     * <pre>
     * 清除所有数据，如果该数据没有找到，返回 <code>null</code>.
     * 否则返回一个长度为 2 的整数数组，其中
     * [0] 标识被清除数据在整体数据中的开始位置, 总是 <code>=0</code>
     * [1] 标识被清除的数据的长度，总是 <code>>0</code> (与此前整个数据的长度相等)
     *
     * 使用示例：
     * <code>
     *
     * int[] positionAndSize = mAdapter.clearAll();
     * if(positionAndSize != null) {
     *     mAdapter.notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
     * }
     *
     * </code>
     * </pre>
     */
    public int[] clearAll() {
        int count = getItemCount();
        if (count <= 0) {
            return null;
        }
        mData.clear();
        return new int[]{0, count};
    }

    @Override
    public int getItemCount() {
        int count = 0;
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            Object groupItems = mData.valueAt(i);
            if (groupItems != null) {
                count += ((ArrayList) groupItems).size();
            }
        }
        return count;
    }

    /**
     * @see #clearGroupItems(int)
     * @see #appendGroupItems(int, Collection)
     */
    public void setGroupItems(int group, Collection items) {
        if (items == null) {
            clearGroupItems(group);
        } else {
            mData.put(group, new ArrayListWrapper(items));
        }
    }

    /**
     * <pre>
     * 向指定组中的指定位置添加数据，如果数据为空，返回 <code>null</code>.
     * 否则返回一个长度为 2 的整数数组，其中
     * [0] 标识添加的数据在整体数据中的开始位置 总是 <code>>=0</code>
     * [1] 标识添加的数据的长度，总是 <code>>0</code>
     *
     * 使用示例：
     * <code>
     *
     * int[] positionAndSize = mAdapter.insertGroupItems(GROUP_DATA, 2, items);
     * if(positionAndSize != null) {
     *     mAdapter.notifyItemRangeInserted(positionAndSize[0], positionAndSize[1]);
     * }
     *
     * </code>
     * </pre>
     */
    public int[] insertGroupItems(int group, int positionInGroup, Collection items) {
        if (items != null && items.size() > 0) {
            Object groupItems = mData.get(group);
            if (groupItems == null) {

                if (positionInGroup != 0) {
                    return null;
                }

                groupItems = new ArrayListWrapper(items);
                mData.put(group, groupItems);
                return new int[]{getGroupPositionStart(group), items.size()};
            } else {
                ArrayList groupItemsList = (ArrayList) groupItems;
                int oldSize = groupItemsList.size();

                if (oldSize < positionInGroup) {
                    return null;
                }

                groupItemsList.addAll(positionInGroup, items);
                return new int[]{getGroupPositionStart(group) + positionInGroup, items.size()};
            }
        } else {
            return null;
        }
    }

    /**
     * <pre>
     * 向指定组中添加数据，如果数据为空，返回 <code>null</code>.
     * 否则返回一个长度为 2 的整数数组，其中
     * [0] 标识添加的数据在整体数据中的开始位置 总是 <code>>=0</code>
     * [1] 标识添加的数据的长度，总是 <code>>0</code>
     *
     * 使用示例：
     * <code>
     *
     * int[] positionAndSize = mAdapter.appendGroupItems(GROUP_DATA, items);
     * if(positionAndSize != null) {
     *     mAdapter.notifyItemRangeInserted(positionAndSize[0], positionAndSize[1]);
     * }
     *
     * </code>
     * </pre>
     */
    public int[] appendGroupItems(int group, Collection items) {
        if (items != null && items.size() > 0) {
            Object groupItems = mData.get(group);
            if (groupItems == null) {
                groupItems = new ArrayListWrapper(items);
                mData.put(group, groupItems);
                return new int[]{getGroupPositionStart(group), items.size()};
            } else {
                ArrayList groupItemsList = (ArrayList) groupItems;
                int positionInGroup = groupItemsList.size();
                groupItemsList.addAll(items);
                return new int[]{getGroupPositionStart(group) + positionInGroup, items.size()};
            }
        } else {
            return null;
        }
    }

    /**
     * 如果没有找到，返回 <code>null</code>.
     */
    public Object getGroupItem(int group, int positionInGroup) {
        if (positionInGroup < 0) {
            return null;
        }

        Object object = mData.get(group);
        if (object == null) {
            return null;
        }

        ArrayList groupItems = (ArrayList) object;
        if (groupItems.size() <= positionInGroup) {
            return null;
        }

        return groupItems.get(positionInGroup);
    }

    /**
     * 如果没有找到，返回 <code>null</code>.
     */
    public Object getItem(int position) {
        if (position < 0) {
            return null;
        }

        int[] groupAndPosition = getGroupAndPosition(position);
        if (groupAndPosition == null) {
            return null;
        }
        return getGroupItem(groupAndPosition[0], groupAndPosition[1]);
    }


    public int getGroupItemViewType(int position, int group, int positionInGroup) {
        return group;
    }

    @Override
    public final int getItemViewType(int position) {
        int[] groupAndPosition = getGroupAndPosition(position);
        return getGroupItemViewType(position, groupAndPosition[0], groupAndPosition[1]);
    }

    public static class EmptyViewHolder extends RecyclerViewGroupHolder {

        public EmptyViewHolder(RecyclerViewGroupAdapter groupAdapter) {
            super(groupAdapter, createView(groupAdapter));
            onHolderUpdate(null, 0);
        }

        @Override
        public final void onHolderUpdate(@Nullable Object object, int position) {
            super.onHolderUpdate(null, 0);
        }

        private static View createView(RecyclerViewGroupAdapter groupAdapter) {
            Space view = new Space(groupAdapter.getRecyclerView().getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(0, 0);
            view.setLayoutParams(layoutParams);
            return view;
        }

    }

    public interface HolderFlags {
        int NONE = 0;
        int UP = ItemTouchHelper.UP;
        int DOWN = ItemTouchHelper.DOWN;
        int LEFT = ItemTouchHelper.LEFT;
        int RIGHT = ItemTouchHelper.RIGHT;
        int START = ItemTouchHelper.START;
        int END = ItemTouchHelper.END;
    }

    public interface HolderDrag extends HolderFlags {
        int getDragFlagsIDLE();

        int getDragFlagsAction();
    }

    public interface HolderSwipe extends HolderFlags {
        int getSwipeFlagsIDLE();

        int getSwipeFlagsAction();
    }

    public interface HolderUpdate {
        void onHolderUpdate(Object object, int position);
    }

    public static class RecyclerViewGroupHolder extends RecyclerView.ViewHolder
            implements HolderUpdate, HolderDrag, HolderSwipe {

        public final View itemView;
        public final RecyclerViewGroupAdapter groupAdapter;

        public RecyclerViewGroupHolder(RecyclerViewGroupAdapter groupAdapter, LayoutInflater inflater, ViewGroup parent, int layout) {
            this(groupAdapter, inflater.inflate(layout, new FrameLayout(groupAdapter.getRecyclerView().getContext()), false));
        }

        public RecyclerViewGroupHolder(RecyclerViewGroupAdapter groupAdapter, View itemView) {
            super(fixItemView(itemView));
            this.itemView = itemView;
            this.groupAdapter = groupAdapter;
        }

        // 对RecyclerView.ViewHolder的itemView设置Visibility不起作用，
        // 此处在外围增加一层FrameLayout，并增加同名成员变量itemView指向该FrameLayout的唯一Child
        private static View fixItemView(View itemView) {
            FrameLayout frameLayout = new FrameLayout(itemView.getContext());
            frameLayout.addView(itemView);

            // 处理layout with, layout height, 除MATCH_PARENT外，一律处理为WRAP_CONTENT
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            ViewGroup.LayoutParams itemViewLayoutParams;
            if ((itemViewLayoutParams = itemView.getLayoutParams()) != null) {
                if (itemViewLayoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                if (itemViewLayoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                }
            }
            frameLayout.setLayoutParams(params);
            return frameLayout;
        }

        public <T extends View> T findViewByID(int id) {
            return ViewUtil.findViewByID(itemView, id);
        }

        @Override
        public void onHolderUpdate(@Nullable Object object, int position) {
            if (object == null) {
                reset(position);
            } else {
                itemView.setVisibility(View.VISIBLE);
                update(object, position);
            }
        }

        private void reset(int position) {
            itemView.setVisibility(View.GONE);
        }

        protected void update(@NonNull Object object, int position) {
        }

        @Override
        public int getDragFlagsIDLE() {
            return getDragFlagsAction();
        }

        @Override
        public int getDragFlagsAction() {
            return NONE;
        }

        @Override
        public int getSwipeFlagsIDLE() {
            return getSwipeFlagsAction();
        }

        @Override
        public int getSwipeFlagsAction() {
            return NONE;
        }
    }

    public static class RecyclerViewGroupTouchHelper extends ItemTouchHelper.Callback {

        private final RecyclerViewGroupAdapter mGroupAdapter;
        private final boolean mLongPressDragEnable;
        private final boolean mItemViewSwipeEnable;

        public RecyclerViewGroupTouchHelper(RecyclerViewGroupAdapter groupAdapter,
                                            boolean longPressDragEnable,
                                            boolean itemViewSwipeEnable) {
            mGroupAdapter = groupAdapter;
            mLongPressDragEnable = longPressDragEnable;
            mItemViewSwipeEnable = itemViewSwipeEnable;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return mLongPressDragEnable;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return mItemViewSwipeEnable;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlagsIDLE = 0;
            int dragFlagsAction = 0;
            int swipeFlagsIDLE = 0;
            int swipeFlagsAction = 0;

            if (viewHolder instanceof HolderDrag) {
                dragFlagsIDLE = ((HolderDrag) viewHolder).getDragFlagsIDLE();
                dragFlagsAction = ((HolderDrag) viewHolder).getDragFlagsAction();
            }

            if (viewHolder instanceof HolderSwipe) {
                swipeFlagsIDLE = ((HolderSwipe) viewHolder).getSwipeFlagsIDLE();
                swipeFlagsAction = ((HolderSwipe) viewHolder).getSwipeFlagsAction();
            }

            return makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, swipeFlagsIDLE | dragFlagsIDLE)
                    | makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlagsAction)
                    | makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, dragFlagsAction);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {

            if (!(viewHolder instanceof HolderDrag)
                    || !(target instanceof HolderDrag)
                    || ((HolderDrag) viewHolder).getDragFlagsAction() == HolderFlags.NONE
                    || ((HolderDrag) target).getDragFlagsAction() == HolderDrag.NONE) {
                return false;
            }

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            int[] movePosition = mGroupAdapter.move(fromPosition, toPosition);
            if (movePosition != null) {
                mGroupAdapter.notifyItemMoved(movePosition[0], movePosition[1]);
                return true;
            }

            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            int[] positionAndSize = mGroupAdapter.removeItem(position);
            if (positionAndSize != null) {
                mGroupAdapter.notifyItemRangeRemoved(positionAndSize[0], positionAndSize[1]);
            }
        }

        public void attachToRecyclerView() {
            new ItemTouchHelper(this).attachToRecyclerView(mGroupAdapter.getRecyclerView());
        }

    }

}
