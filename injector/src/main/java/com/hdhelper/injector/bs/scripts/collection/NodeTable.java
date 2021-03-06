package com.hdhelper.injector.bs.scripts.collection;

import com.bytescript.lang.BField;
import com.bytescript.lang.ByteScript;
import com.hdhelper.agent.services.RSNode;
import com.hdhelper.agent.services.RSNodeTable;

@ByteScript(name = "NodeTable")
public class NodeTable implements RSNodeTable {

    @BField Node[] buckets;
    @BField int size;
    @BField int index;

    public<T extends RSNode> T get(long key) {
        int capacity = index;
        Node[] buckets = this.buckets;
        Node last = buckets[(int) (key & (long) (capacity - 1))];
        Node current;
        for (current = last.next; current != last; current = current.next) {
            if (current.key == key) {
                return (T) current;
            }
        }
        return null;
    }



    @Override
    public RSNode[] getBuckets() {
        return buckets;
    }

    @Override
    public int getCapacity() {
        return index; //TODO its swapped in the updater
    }
}
