package hu.danielb.raceadmin.ui.components.table.tablemodels;

import java.util.Comparator;
import java.util.function.Function;

public class ColumnModel<T> {

    private Getter<T> getter;
    private String name;
    private int width;
    private int ordinal;
    private Comparator<? super T> comparator;
    private boolean button;
    private boolean sortable;

    private ColumnModel(String name, int width, int ordinal, boolean button, boolean sortable, Getter<T> getter, Comparator<? super T> comparator) {
        this.name = name;
        this.width = width;
        this.ordinal = ordinal;
        this.getter = getter;
        this.button = button;
        this.comparator = comparator;
        this.sortable = sortable;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getOrdinal() {
        return ordinal;
    }

    Object getValue(T data) {
        if (getter == null || data == null) {
            return null;
        }
        return getter.getValue(data);
    }

    public boolean isButton() {
        return button;
    }

    public Comparator<? super T> getComparator(boolean sortBackwards) {
        if (comparator != null && sortBackwards) {
            return comparator.reversed();
        }
        return comparator;
    }

    public boolean isSortable() {
        return sortable;
    }

    interface Getter<T> {
        Object getValue(T data);
    }

    public static class Builder<T> {

        private String mName = "";
        private int mWidth = 0;
        private int mOrdinal = 0;
        private boolean mButton = false;
        private boolean mSortable = true;
        private Getter<T> mGetter = null;
        private Comparator<? super T> mComparator = null;

        public Builder() {
        }

        public Builder<T> setName(String name) {
            mName = name;
            return this;
        }

        public Builder<T> setWidth(int width) {
            mWidth = width;
            return this;
        }

        public Builder<T> setOrdinal(int ordinal) {
            mOrdinal = ordinal;
            return this;
        }

        public Builder<T> setButton(boolean button) {
            mButton = button;
            return this;
        }

        public Builder<T> setSortable(boolean sortable) {
            mSortable = sortable;
            return this;
        }

        public Builder<T> setGetter(Getter<T> getter) {
            mGetter = getter;
            return this;
        }

        public <U extends Comparable<? super U>> Builder<T> setComparator(Function<? super T, ? extends U> extractor) {
            mComparator = Comparator.<T, U>comparing(extractor, Comparator.nullsLast(Comparator.naturalOrder()));
            return this;
        }

        public ColumnModel<T> build() {
            return new ColumnModel<>(mName, mWidth, mOrdinal, mButton, mSortable, mGetter, mComparator);
        }

    }
}
