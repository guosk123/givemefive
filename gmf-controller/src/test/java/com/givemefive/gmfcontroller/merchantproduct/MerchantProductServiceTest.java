package com.givemefive.gmfcontroller.merchantproduct;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({MerchantProductService.class, MerchantProductCrypto.class})
class MerchantProductServiceTest {

    @Autowired
    private MerchantProductService service;

    @Autowired
    private MerchantProductRepository repository;

    @Test
    void createEncryptsFieldsAndListOnlyReturnsCurrentUserProducts() {
        MerchantProductResponse saved = service.create("openid-001", new MerchantProductRequest(
                "柠檬茶",
                new BigDecimal("3.50"),
                new BigDecimal("8.00"),
                12,
                "杯",
                "冷藏"
        ));
        service.create("openid-002", new MerchantProductRequest(
                "其他商品",
                new BigDecimal("1.00"),
                new BigDecimal("2.00"),
                99,
                "件",
                "other user"
        ));

        MerchantProductEntity entity = repository.findById(saved.id()).orElseThrow();
        assertThat(entity.getProductNameEncrypted()).isNotEqualTo("柠檬茶");
        assertThat(entity.getPurchasePriceEncrypted()).isNotEqualTo("3.50");
        assertThat(entity.getSalePriceEncrypted()).isNotEqualTo("8.00");
        assertThat(entity.getStockQuantityEncrypted()).isNotEqualTo("12");
        assertThat(entity.getRemarkEncrypted()).isNotEqualTo("冷藏");

        MerchantProductListResponse list = service.list("openid-001");

        assertThat(list.products()).singleElement().satisfies(product -> {
            assertThat(product.productName()).isEqualTo("柠檬茶");
            assertThat(product.purchasePrice()).isEqualByComparingTo("3.50");
            assertThat(product.salePrice()).isEqualByComparingTo("8.00");
            assertThat(product.stockQuantity()).isEqualTo(12);
            assertThat(product.grossProfit()).isEqualByComparingTo("4.50");
            assertThat(product.stockCost()).isEqualByComparingTo("42.00");
            assertThat(product.stockRevenue()).isEqualByComparingTo("96.00");
        });
        assertThat(list.productCount()).isEqualTo(1);
        assertThat(list.stockTotal()).isEqualTo(12);
        assertThat(list.stockCostTotal()).isEqualByComparingTo("42.00");
        assertThat(list.stockRevenueTotal()).isEqualByComparingTo("96.00");
        assertThat(list.grossProfitTotal()).isEqualByComparingTo("54.00");
    }

    @Test
    void updateAndDeleteOnlyAffectCurrentUserProducts() {
        MerchantProductResponse ownProduct = service.create("openid-001", new MerchantProductRequest(
                "红茶",
                new BigDecimal("2.00"),
                new BigDecimal("6.00"),
                10,
                "杯",
                ""
        ));
        MerchantProductResponse otherProduct = service.create("openid-002", new MerchantProductRequest(
                "咖啡",
                new BigDecimal("4.00"),
                new BigDecimal("12.00"),
                5,
                "杯",
                ""
        ));

        MerchantProductResponse updated = service.update(
                "openid-001",
                ownProduct.id(),
                new MerchantProductRequest(
                        "红茶升级",
                        new BigDecimal("2.50"),
                        new BigDecimal("7.00"),
                        8,
                        "杯",
                        "新包装"
                ));

        assertThat(updated.id()).isEqualTo(ownProduct.id());
        assertThat(updated.productName()).isEqualTo("红茶升级");
        assertThat(updated.stockQuantity()).isEqualTo(8);

        service.delete("openid-001", otherProduct.id());
        assertThat(repository.findById(otherProduct.id())).isPresent();

        service.delete("openid-001", ownProduct.id());
        assertThat(repository.findById(ownProduct.id())).isEmpty();
    }
}
