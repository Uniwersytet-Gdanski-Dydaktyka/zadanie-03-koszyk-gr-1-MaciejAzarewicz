import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cart {
    // Domyslne sortowanie z zadania: cena malejaco, potem nazwa alfabetycznie
    public static final Comparator<Product> DEFAULT_COMPARATOR = Comparator
            .comparingDouble(Product::getPrice)
            .reversed()
            .thenComparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Product::getCode);

    private final List<Product> products = new ArrayList<>();
    private final List<Promotion> promotions = new ArrayList<>();
    private Comparator<Product> comparator = DEFAULT_COMPARATOR;

    public Cart() {
    }

    // Tworzy koszyk z gotowej kolekcji produktow
    public Cart(Collection<Product> products) {
        addProducts(products);
    }

    // Dodaje produkt, a null ignoruje jako sytuacje brzegowa
    public void addProduct(Product product) {
        if (product != null) {
            products.add(product);
        }
    }

    // Dodaje wiele produktow, korzystajac z addProduct
    public void addProducts(Collection<Product> products) {
        if (products != null) {
            for (Product product : products) {
                addProduct(product);
            }
        }
    }

    // Zwraca posortowana kopie produktow
    public List<Product> getProducts() {
        return sorted(products);
    }

    // Pozwala zmienic sposob sortowania
    public void setComparator(Comparator<Product> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator nie moze byc null");
        }
        this.comparator = comparator;
    }

    // Wyszukuje najtanszy produkt w koszyku
    public Product findCheapest() {
        return findCheapest(products);
    }

    // Wyszukuje najdrozszy produkt w koszyku
    public Product findMostExpensive() {
        return findMostExpensive(products);
    }

    // Zwraca n najtanszych produktow
    public List<Product> findCheapest(int amount) {
        return findCheapest(products, amount);
    }

    // Zwraca n najdrozszych produktow
    public List<Product> findMostExpensive(int amount) {
        return findMostExpensive(products, amount);
    }

    // Liczy sume zwyklych cen, bez promocji
    public double totalPrice() {
        return totalPrice(products);
    }

    // Sumuje ceny po promocjach
    public double totalDiscountPrice() {
        return totalDiscountPrice(applyPromotions());
    }

    // Dodaje nowa promocje
    public void addPromotion(Promotion promotion) {
        if (promotion != null) {
            promotions.add(promotion);
        }
    }

    // Usuwa wybrana promocje
    public void removePromotion(Promotion promotion) {
        promotions.remove(promotion);
    }

    // Czyści wszystkie promocje
    public void clearPromotions() {
        promotions.clear();
    }

    // Aplikuje promocje w kolejnosci dodania
    public List<Product> applyPromotions() {
        List<Product> result = resetDiscounts(products);
        for (Promotion promotion : promotions) {
            result = clean(promotion.apply(result));
        }
        return sorted(result);
    }

    // Sprawdza wszystkie możliwe kombinacje promocji i wybiera tę, która daje najniższą łączną cenę
    public List<Product> applyBestPromotions() {
        if (promotions.isEmpty()) {
            return sorted(resetDiscounts(products));
        }

        List<List<Promotion>> orders = new ArrayList<>();
        makeOrders(new ArrayList<Promotion>(), new HashSet<Integer>(), orders);

        List<Product> bestProducts = new ArrayList<>();
        double bestPrice = Double.MAX_VALUE;

        for (List<Promotion> order : orders) {
            List<Product> result = resetDiscounts(products);
            for (Promotion promotion : order) {
                result = clean(promotion.apply(result));
            }

            double price = totalDiscountPrice(result);
            if (price < bestPrice) {
                bestPrice = price;
                bestProducts = result;
            }
        }

        return sorted(bestProducts);
    }

    // Statyczne wyszukiwanie najtanszego produktu
    public static Product findCheapest(Collection<Product> products) {
        List<Product> cleanProducts = clean(products);
        if (cleanProducts.isEmpty()) {
            return null;
        }
        return Collections.min(cleanProducts, Comparator
                .comparingDouble(Product::getPrice)
                .thenComparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Product::getCode));
    }

    // Statyczne wyszukiwanie najdrozszyego produktu
    public static Product findMostExpensive(Collection<Product> products) {
        List<Product> cleanProducts = clean(products);
        if (cleanProducts.isEmpty()) {
            return null;
        }
        return Collections.max(cleanProducts, Comparator
                .comparingDouble(Product::getPrice)
                .thenComparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Product::getCode));
    }

    // Sortuje rosnaco po cenie i bierze pierwsze n produktow
    public static List<Product> findCheapest(Collection<Product> products, int amount) {
        List<Product> result = clean(products);
        result.sort(Comparator
                .comparingDouble(Product::getPrice)
                .thenComparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Product::getCode));
        return limit(result, amount);
    }

    // Sortuje malejaco po cenie i bierze pierwsze n produktow
    public static List<Product> findMostExpensive(Collection<Product> products, int amount) {
        List<Product> result = clean(products);
        result.sort(DEFAULT_COMPARATOR);
        return limit(result, amount);
    }

    // Sumuje zwykle ceny produktow
    public static double totalPrice(Collection<Product> products) {
        double sum = 0;
        for (Product product : clean(products)) {
            sum += product.getPrice();
        }
        return sum;
    }

    // Sumuje ceny po promocjach
    public static double totalDiscountPrice(Collection<Product> products) {
        double sum = 0;
        for (Product product : clean(products)) {
            sum += product.getDiscountPrice();
        }
        return sum;
    }

    // Rekurencyjnie tworzy wszystkie możliwe kolejności promocji
    private void makeOrders(List<Promotion> current, Set<Integer> used, List<List<Promotion>> orders) {
        if (current.size() == promotions.size()) {
            orders.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < promotions.size(); i++) {
            if (!used.contains(i)) {
                used.add(i);
                current.add(promotions.get(i));
                makeOrders(current, used, orders);
                current.remove(current.size() - 1);
                used.remove(i);
            }
        }
    }

    // Sortuje i zwraca liste
    private List<Product> sorted(Collection<Product> products) {
        List<Product> result = clean(products);
        result.sort(comparator);
        return Collections.unmodifiableList(result);
    }

    // Przywraca discountPrice do ceny podstawowej dla kazdego produktu
    private static List<Product> resetDiscounts(Collection<Product> products) {
        List<Product> result = new ArrayList<>();
        for (Product product : clean(products)) {
            result.add(product.resetDiscount());
        }
        return result;
    }

    // Czysci liste z nulli
    private static List<Product> clean(Collection<Product> products) {
        List<Product> result = new ArrayList<>();
        if (products == null) {
            return result;
        }
        for (Product product : products) {
            if (product != null) {
                result.add(product);
            }
        }
        return result;
    }

    // Obcina liste do podanej liczby elementow
    private static List<Product> limit(List<Product> products, int amount) {
        if (amount <= 0) {
            return Collections.emptyList();
        }
        if (amount >= products.size()) {
            return Collections.unmodifiableList(products);
        }
        return Collections.unmodifiableList(new ArrayList<>(products.subList(0, amount)));
    }

    // Wspolny interfejs dla wszystkich promocji
    public interface Promotion {
        List<Product> apply(List<Product> products);
    }

    public static class PercentageOverAmountPromotion implements Promotion {
        private final double threshold;
        private final double percent;

        public PercentageOverAmountPromotion(double threshold, double percent) {
            if (threshold < 0 || percent < 0 || percent > 100) {
                throw new IllegalArgumentException("Niepoprawne wartosci promocji");
            }
            this.threshold = threshold;
            this.percent = percent;
        }

        // Obniza cene kazdego produktu o podany procent
        @Override
        public List<Product> apply(List<Product> products) {
            List<Product> result = clean(products);
            if (totalDiscountPrice(result) <= threshold) {
                return result;
            }

            double factor = 1 - percent / 100;
            List<Product> discounted = new ArrayList<>();
            for (Product product : result) {
                discounted.add(product.withDiscountPrice(product.getDiscountPrice() * factor));
            }
            return discounted;
        }
    }

    public static class CheapestFreePromotion implements Promotion {
        private final int requiredProducts;

        public CheapestFreePromotion(int requiredProducts) {
            if (requiredProducts <= 0) {
                throw new IllegalArgumentException("Liczba produktow musi byc dodatnia");
            }
            this.requiredProducts = requiredProducts;
        }

        // Przy wymaganej liczbie produktow ustawia najtanszemu discountPrice na 0
        @Override
        public List<Product> apply(List<Product> products) {
            List<Product> result = clean(products);
            if (result.size() < requiredProducts) {
                return result;
            }

            Product cheapest = Collections.min(result, Comparator
                    .comparingDouble(Product::getDiscountPrice)
                    .thenComparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(Product::getCode));

            List<Product> discounted = new ArrayList<>();
            boolean used = false;
            for (Product product : result) {
                if (!used && product.equals(cheapest)) {
                    discounted.add(product.withDiscountPrice(0));
                    used = true;
                } else {
                    discounted.add(product);
                }
            }
            return discounted;
        }
    }
    // Darmowy prezent 
    public static class FreeGiftOverAmountPromotion implements Promotion {
        private final double threshold;
        private final Product gift;

        public FreeGiftOverAmountPromotion(double threshold, Product gift) {
            if (threshold < 0 || gift == null) {
                throw new IllegalArgumentException("Niepoprawne wartosci promocji");
            }
            this.threshold = threshold;
            this.gift = gift.withDiscountPrice(0);
        }

        // Po przekroczeniu progu dodaje darmowy prezent, jesli nie ma go juz w koszyku
        @Override
        public List<Product> apply(List<Product> products) {
            List<Product> result = clean(products);
            if (totalDiscountPrice(result) <= threshold || hasProduct(result, gift.getCode())) {
                return result;
            }
            result.add(gift);
            return result;
        }

        // Sprawdza, czy produkt o danym kodzie jest juz na liscie
        private boolean hasProduct(List<Product> products, String code) {
            for (Product product : products) {
                if (product.getCode().equals(code)) {
                    return true;
                }
            }
            return false;
        }
    }
    // Kupon rabatowy na konkretny produkt
    public static class CouponPromotion implements Promotion {
        private final String productCode;
        private final double percent;

        public CouponPromotion(String productCode, double percent) {
            if (productCode == null || productCode.trim().isEmpty() || percent < 0 || percent > 100) {
                throw new IllegalArgumentException("Niepoprawne wartosci kuponu");
            }
            this.productCode = productCode;
            this.percent = percent;
        }

        // Obniza cene tylko pierwszego produktu o wskazanym kodzie
        @Override
        public List<Product> apply(List<Product> products) {
            List<Product> result = clean(products);
            double factor = 1 - percent / 100;
            List<Product> discounted = new ArrayList<>();
            boolean used = false;

            for (Product product : result) {
                if (!used && product.getCode().equals(productCode)) {
                    discounted.add(product.withDiscountPrice(product.getDiscountPrice() * factor));
                    used = true;
                } else {
                    discounted.add(product);
                }
            }

            return discounted;
        }
    }
}
