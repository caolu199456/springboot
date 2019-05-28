package com.example.util.common;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class PageUtils<T> {
    /**
     * 当前第几页
     */
    private Integer pageNo = 1;
    /**
     * 每页多少条
     */
    private Integer pageSize = 10;
    /**
     * 总共多少页
     */
    private Integer totalPages=0;
    /**
     * 页码最多能显示多少个
     */
    private Integer showPages = 5;
    /**
     * 总数多少个
     */
    private Integer total=0;

    /**
     * 页码开始数和结束数  比如当前显示到第10页  startNo=8 endNo=12
     */
    private Integer startNo;
    private Integer endNo;

    private boolean hasPrevious;
    private boolean hasNext;

    private List<T> records;

    public PageUtils(Integer pageNo, Integer pageSize, Integer total, List<T> records) {
        this(pageNo, pageSize, 5, total, records);
    }

    public PageUtils(Integer pageNo, Integer pageSize, Integer showPages, Integer total, List<T> records) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.showPages = showPages;
        this.total = total;
        this.records = records;

        set();
    }

    private void set() {
        if (this.total == null || this.pageNo == null || this.pageSize == null) {
            return;
        }

        if (this.total % this.pageSize == 0) {
            this.totalPages = this.total / this.pageSize;
        }else {
            this.totalPages = this.total / this.pageSize + 1;
        }

        this.setHasPrevious(this.pageNo > 1);
        this.setHasNext(this.pageNo < this.totalPages);

        if (this.totalPages <= this.showPages) {
            this.startNo = 1;
            this.endNo = this.totalPages;
        }else {
            if (this.pageNo >= (this.showPages / 2 + 1)) {
                this.startNo = this.pageNo - this.showPages / 2 + 1;
                if ((this.pageNo + this.showPages / 2) <= this.totalPages) {
                    this.endNo = this.pageNo + this.showPages / 2;
                }else {
                    this.startNo = this.totalPages - (this.showPages - 1);
                    this.endNo = this.totalPages;
                }
            }
        }
    }

    public static void main(String[] args) {
        PageUtils<Object> pageUtils = new PageUtils<>(28, 10,10, 300, null);
        System.out.println(pageUtils);
    }


}
