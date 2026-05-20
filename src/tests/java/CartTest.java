import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartTest {
    // Sprawdza wyszukiwanie najtanszych i najdrozszych produktow
    @Test
    void findsCheapestAndMostExpensiveProducts() {
        Cart cart = exampleCart();

        assertEquals("P2", cart.findCheapest().getCode());
        assertEquals("P1", cart.findMostExpensive().getCode());
        assertEquals(Arrays.asList("P2", "P4"), codes(cart.findCheapest(2)));
        assertEquals(Arrays.asList("P1", "P4"), codes(cart.findMostExpensive(2)));
    }

    // Sprawdza domyslne sortowanie: cena malejaco, potem nazwa
    @Test
    void sortsByPriceDescendingAndNameAscendingByDefault() {
        Cart cart = new Cart(Arrays.asList(
                new Product("P1", "Zeszyt", 10),
                new Product("P2", "Album", 10),
                new Product("P3", "Monitor", 100)
        ));

        assertEquals(Arrays.asList("P3", "P2", "P1"), codes(cart.getProducts()));
    }

    // Sprawdza mozliwosc zmiany sortowania przez Comparator
    @Test
    void allowsChangingSortOrder() {
        Cart cart = exampleCart();

        cart.setComparator((first, second) -> first.getName().compareTo(second.getName()));

        assertEquals(Arrays.asList("P4", "P3", "P1", "P2"), codes(cart.getProducts()));
    }

    // Sprawdza sume cen bez promocji
    @Test
    void calculatesTotalPrice() {
        Cart cart = exampleCart();

        assertEquals(750, cart.totalPrice(), 0.001);
    }

    // Sprawdza promocje procentowa po przekroczeniu progu
    @Test
    void appliesPercentagePromotionOverAmount() {
        Cart cart = new Cart(Arrays.asList(
                new Product("P1", "Monitor", 200),
                new Product("P2", "Klawiatura", 150)
        ));
        cart.addPromotion(new Cart.PercentageOverAmountPromotion(300, 5));

        assertEquals(332.5, cart.totalDiscountPrice(), 0.001);
    }

    // Sprawdza promocje najtanszy produkt grati
    @Test
    void appliesCheapestFreePromotion() {
        Cart cart = new Cart(Arrays.asList(
                new Product("P1", "Monitor", 200),
                new Product("P2", "Mysz", 50),
                new Product("P3", "Klawiatura", 100)
        ));
        cart.addPromotion(new Cart.CheapestFreePromotion(3));

        assertEquals(300, cart.totalDiscountPrice(), 0.001);
    }

    // Sprawdza dodanie darmowego prezentu po przekroczeniu progu
    @Test
    void addsGiftOverAmount() {
        Cart cart = new Cart(Collections.singletonList(new Product("P1", "Monitor", 250)));
        cart.addPromotion(new Cart.FreeGiftOverAmountPromotion(200, new Product("GIFT", "Kubek firmowy", 0)));

        assertEquals(Arrays.asList("P1", "GIFT"), codes(cart.applyPromotions()));
        assertEquals(250, cart.totalDiscountPrice(), 0.001);
    }

    // Sprawdza kupon procentowy na wybrany produkt
    @Test
    void appliesCouponToSelectedProduct() {
        Cart cart = new Cart(Arrays.asList(
                new Product("P1", "Monitor", 200),
                new Product("P2", "Mysz", 50)
        ));
        cart.addPromotion(new Cart.CouponPromotion("P1", 30));

        assertEquals(190, cart.totalDiscountPrice(), 0.001);
    }

    // Sprawdza usuwanie promocji z koszyka
    @Test
    void removesPromotion() {
        Cart cart = new Cart(Arrays.asList(
                new Product("P1", "Monitor", 200),
                new Product("P2", "Mysz", 50)
        ));
        Cart.Promotion promotion = new Cart.CouponPromotion("P1", 30);

        cart.addPromotion(promotion);
        cart.removePromotion(promotion);

        assertEquals(250, cart.totalDiscountPrice(), 0.001);
    }

    // Sprawdza puste dane, null i niepoprawne n
    @Test
    void handlesEdgeCases() {
        Cart cart = new Cart(Arrays.asList(null, new Product("P1", "Gratis", 0)));
        cart.addProduct(null);
        cart.addPromotion(null);

        assertEquals(0, new Cart().totalPrice(), 0.001);
        assertEquals(0, cart.totalPrice(), 0.001);
        assertEquals(0, cart.findCheapest(0).size());
        assertEquals(0, Cart.findCheapest(null, 5).size());
        assertNull(Cart.findCheapest(Collections.emptyList()));
    }

    // Sprawdza, czy promocje na pustym koszyku nie powoduja bledu
    @Test
    void appliesPromotionToEmptyCart() {
        Cart cart = new Cart();
        cart.addPromotion(new Cart.PercentageOverAmountPromotion(300, 5));
        cart.addPromotion(new Cart.CheapestFreePromotion(3));
        cart.addPromotion(new Cart.FreeGiftOverAmountPromotion(200, new Product("GIFT", "Kubek firmowy", 0)));

        assertEquals(0, cart.applyPromotions().size());
        assertEquals(0, cart.totalDiscountPrice(), 0.001);
    }

    // Sprawdza walidacje danych produktu
    @Test
    void validatesProductData() {
        assertThrows(IllegalArgumentException.class, () -> new Product("", "Nazwa", 1));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "", 1));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Nazwa", -1));
    }

    // Sprawdza wybor najlepszej kolejnosci promocji
    @Test
    void choosesBestPromotionOrder() {
        Cart cart = new Cart(Arrays.asList(
                new Product("P1", "A", 200),
                new Product("P2", "B", 100),
                new Product("P3", "C", 50)
        ));
        cart.addPromotion(new Cart.PercentageOverAmountPromotion(300, 10));
        cart.addPromotion(new Cart.CheapestFreePromotion(3));

        assertEquals(270, Cart.totalDiscountPrice(cart.applyBestPromotions()), 0.001);
    }

    private Cart exampleCart() {
        return new Cart(Arrays.asList(
                new Product("P1", "Monitor", 500),
                new Product("P2", "Mysz", 50),
                new Product("P3", "Klawiatura", 100),
                new Product("P4", "Adapter", 100)
        ));
    }

    private List<String> codes(List<Product> products) {
        java.util.ArrayList<String> codes = new java.util.ArrayList<>();
        for (Product product : products) {
            codes.add(product.getCode());
        }
        return codes;
    }
}
