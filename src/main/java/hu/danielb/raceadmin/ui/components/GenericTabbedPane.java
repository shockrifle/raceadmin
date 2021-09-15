package hu.danielb.raceadmin.ui.components;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class GenericTabbedPane<T, K> extends JTabbedPane {

    private T mData;
    private final Map<Integer, Tab<K>> tabs;

    public GenericTabbedPane(T data) {
        super();
        this.mData = data;
        tabs = new HashMap<>();
    }

    public void addTab(K id, String title, Component component) {
        super.addTab(title, component);
        tabs.put(indexOfComponent(component), new Tab<>(id, title, component));
    }

    public T getData() {
        return mData;
    }

    public void setData(T model) {
        this.mData = model;
    }

    public Tab<K> getTab(int position) {
        return tabs.get(position);
    }

    public static class Tab<P> {

        P id;
        String title;
        Component component;

        Tab(P id, String title, Component component) {
            this.id = id;
            this.title = title;
            this.component = component;
        }

        public P getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Component getComponent() {
            return component;
        }
    }
}
