package com.dave.fish.ui.main.menu;

import android.view.ViewGroup;

public abstract class DrawerItem<T extends DrawerAdapter.ViewHolder> {

    boolean isChecked;

    public abstract T createViewHolder(ViewGroup parent);

    public abstract void bindViewHolder(T holder);

    public DrawerItem setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }

    boolean isChecked() {
        return isChecked;
    }

    boolean isSelectable() {
        return true;
    }

}
