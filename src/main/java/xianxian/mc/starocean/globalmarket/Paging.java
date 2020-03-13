package xianxian.mc.starocean.globalmarket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Paging<T> {
    private final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    
    private final List<List<T>> elementsPages = new ArrayList<List<T>>();
    private final int elementsPerPage;
    private final Lock lock = new ReentrantLock();
    
    public Paging(int elementsPerPage) {
        this.elementsPerPage = elementsPerPage;
    }
    
    public void page(List<T> records) {
        EXECUTOR.execute(()->{
            lock.lock();
            elementsPages.forEach((page)->page.clear());
            int size = records.size();
            int pages = size / elementsPerPage + (size % elementsPerPage > 0 ? 1 : 0);
            if (pages == 0) {
                elementsPages.clear();
            } else {
                int pagesSize;
                while ((pagesSize = elementsPages.size()) > pages) {
                    elementsPages.remove(pagesSize - 1);
                }
            }
            //int currentPage = 1;
            for (int i = 0, recordPagesSize = elementsPages.size(); i < pages; i ++) {
                List<T> page;
                // 当当前页小于缓存页的大小则重用
                if (i < recordPagesSize) {
                    page = elementsPages.get(i);
                    if (page == null) {
                        page = new ArrayList<T>(elementsPerPage);
                        elementsPages.set(i, page);
                    } else {
                        page = elementsPages.get(i);
                        page.clear();
                    }
                } else {
                    page = new ArrayList<T>(elementsPerPage);
                    elementsPages.add(page);
                }
                
                for (int j = 0; j < elementsPerPage; j++) {
                    int currentIndex = i * elementsPerPage + j;
                    if (currentIndex < size) {
                        page.add(records.get(currentIndex));
                    } else {
                        break;
                    }
                }
            }
            lock.unlock();
        });
    }
    
    public void append(T record) {
        int size = elementsPages.size();
        
        if (size == 0) {
            List<T> newPage = new ArrayList<>();
            newPage.add(record);
            this.elementsPages.add(newPage);
            return;
        }
        List<T> page = this.elementsPages.get(this.elementsPages.size() - 1);
        if (page.size() + 1 > this.elementsPerPage) {
            List<T> newPage = new ArrayList<>();
            newPage.add(record);
            this.elementsPages.add(newPage);
        } else {
            page.add(record);
        }
    }
    
    public List<List<T>> pages() {
        return elementsPages;
    }

}
