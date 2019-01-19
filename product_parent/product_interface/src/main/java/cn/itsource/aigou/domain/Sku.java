package cn.itsource.aigou.domain;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * SKU
 * </p>
 *
 * @author yhptest
 * @since 2019-01-19
 */
@TableName("t_sku")
public class Sku extends Model<Sku> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long createTime;
    private Long updateTime;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * SKU编码
     */
    private String skuCode;
    private String skuName;
    /**
     * 市场价
     */
    private Integer marketPrice;
    /**
     * 优惠价
     */
    private Integer price;
    /**
     * 成本价
     */
    private Integer costPrice;
    /**
     * 销量
     */
    private Integer saleCount;
    /**
     * 排序
     */
    private Integer sortIndex;
    /**
     * 可用库存
     */
    private Integer availableStock;
    /**
     * 锁定库存
     */
    private Integer frozenStock;
    /**
     * SKU属性摘要
     */
    private String skuValues;
    /**
     * 预览图
     */
    private String skuMainPic;
    private String indexs;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Integer getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Integer marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Integer costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Integer saleCount) {
        this.saleCount = saleCount;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getFrozenStock() {
        return frozenStock;
    }

    public void setFrozenStock(Integer frozenStock) {
        this.frozenStock = frozenStock;
    }

    public String getSkuValues() {
        return skuValues;
    }

    public void setSkuValues(String skuValues) {
        this.skuValues = skuValues;
    }

    public String getSkuMainPic() {
        return skuMainPic;
    }

    public void setSkuMainPic(String skuMainPic) {
        this.skuMainPic = skuMainPic;
    }

    public String getIndexs() {
        return indexs;
    }

    public void setIndexs(String indexs) {
        this.indexs = indexs;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Sku{" +
        ", id=" + id +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", productId=" + productId +
        ", skuCode=" + skuCode +
        ", skuName=" + skuName +
        ", marketPrice=" + marketPrice +
        ", price=" + price +
        ", costPrice=" + costPrice +
        ", saleCount=" + saleCount +
        ", sortIndex=" + sortIndex +
        ", availableStock=" + availableStock +
        ", frozenStock=" + frozenStock +
        ", skuValues=" + skuValues +
        ", skuMainPic=" + skuMainPic +
        ", indexs=" + indexs +
        "}";
    }
}
