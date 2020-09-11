package com.konopka.workshops.logic;

import com.konopka.workshops.domain.*;
import com.konopka.workshops.domain.Currency;
import com.konopka.workshops.mock.HoldingMockGenerator;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorkShop {
    /**
     * Lista holdingów wczytana z mocka.
     */
    private final List<Holding> holdings;

    private final Predicate<User> isWoman = null;

    public WorkShop() {
        final HoldingMockGenerator holdingMockGenerator = new HoldingMockGenerator();
        holdings = holdingMockGenerator.generate();
    }

    /**
     * Metoda zwraca liczbę holdingów w których jest przynajmniej jedna firma.
     */
    public long getHoldingsWhereAreCompanies() {
        return holdings.stream().filter(holding -> holding.getCompanies().size() > 0).count();
    }

    /**
     * Zwraca nazwy wszystkich holdingów pisane z małej litery w formie listy.
     */
    public List<String> getHoldingNames() {
        return holdings.stream().map(holding -> holding.getName().toLowerCase()).collect(Collectors.toList());
    }

    /**
     * Zwraca nazwy wszystkich holdingów sklejone w jeden string i posortowane.
     * String ma postać: (Coca-Cola, Nestle, Pepsico)
     */
    public String getHoldingNamesAsString() {
        return holdings.stream().map(holding -> holding.getName()).sorted().collect(Collectors.joining(", ","(",")"));
    }

    /**
     * Zwraca liczbę firm we wszystkich holdingach.
     */
    public long getCompaniesAmount() {
        return holdings.stream().mapToLong(holding -> holding.getCompanies().size())
                .sum();
    }

    /**
     * Zwraca liczbę wszystkich pracowników we wszystkich firmach.
     */
    public long getAllUserAmount() {
        return holdings.stream().flatMap(holding -> holding.getCompanies().stream())
                .mapToLong(company -> company.getUsers().size()).sum();
    }

    /**
     * Zwraca listę wszystkich nazw firm w formie listy. Tworzenie strumienia firm umieść w osobnej metodzie którą
     * później będziesz wykorzystywać.
     */
    public List<String> getAllCompaniesNames() {
        return holdings.stream().flatMap(holding -> holding.getCompanies().stream()).map(company -> company.getName())
                .collect(Collectors.toList());
    }

    /**
     * Zwraca listę wszystkich firm jako listę, której implementacja to LinkedList. Obiektów nie przepisujemy
     * po zakończeniu działania strumienia.
     */
    public LinkedList<String> getAllCompaniesNamesAsLinkedList() {
        return holdings.stream().flatMap(holding -> holding.getCompanies().stream()).map(company -> company.getName())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Zwraca listę firm jako string gdzie poszczególne firmy są oddzielone od siebie znakiem "+"
     */
    public String getAllCompaniesNamesAsString() {
        return holdings.stream().flatMap(holding -> holding.getCompanies().stream()).map(company -> company.getName())
                .collect(Collectors.joining("+"));
    }

    /**
     * Zwraca listę firm jako string gdzie poszczególne firmy są oddzielone od siebie znakiem "+".
     * Używamy collect i StringBuilder.
     * <p>
     * UWAGA: Zadanie z gwiazdką. Nie używamy zmiennych.
     */
    public String getAllCompaniesNamesAsStringUsingStringBuilder() {
        return "";
    }

    /**
     * Zwraca liczbę wszystkich rachunków, użytkowników we wszystkich firmach.
     */
    public long getAllUserAccountsAmount() {
        return holdings.stream().flatMap(holding -> holding.getCompanies().stream())
                .flatMap(company -> company.getUsers().stream()).mapToLong(user -> user.getAccounts().size())
                .sum();
    }

    /**
     * Zwraca listę wszystkich walut w jakich są rachunki jako string, w którym wartości
     * występują bez powtórzeń i są posortowane.
     */
    public String getAllCurrencies() {
        return holdings.stream().flatMap(company -> company.getCompanies().stream())
                .flatMap(user -> user.getUsers().stream()).flatMap(currency -> currency.getAccounts().stream())
                .map(account -> account.getCurrency()).map(c -> Objects.toString(c,null))
                .collect(Collectors.toList()).stream().distinct().sorted().collect(Collectors.joining(", "));
    }

    /**
     * Metoda zwraca analogiczne dane jak getAllCurrencies, jednak na utworzonym zbiorze nie uruchamiaj metody
     * stream, tylko skorzystaj z  Stream.generate. Wspólny kod wynieś do osobnej metody.
     *
     * @see #getAllCurrencies()
     */
    public String getAllCurrenciesUsingGenerate() {
        return null;
    }

    /**
     * Zwraca liczbę kobiet we wszystkich firmach. Powtarzający się fragment kodu tworzący strumień użytkowników umieść
     * w osobnej metodzie. Predicate określający czy mamy do czynienia z kobietą niech będzie polem statycznym w klasie.
     */
    public long getWomanAmount() {
        return holdings.stream().flatMap(holding -> holding.getCompanies().stream())
                .flatMap(companies -> companies.getUsers().stream())
                .filter(user -> user.getSex() == Sex.WOMAN).count();
    }


    /**
     * Przelicza kwotę na rachunku na złotówki za pomocą kursu określonego w enum Currency.
     */
    public BigDecimal getAccountAmountInPLN(final Account account) {
        return account.getAmount().multiply(BigDecimal.valueOf(account.getCurrency().rate)).
                round(new MathContext(4, RoundingMode.HALF_UP));
    }

    /**
     * Przelicza kwotę na podanych rachunkach na złotówki za pomocą kursu określonego w enum Currency i sumuje ją.
     */
    public BigDecimal getTotalCashInPLN(final List<Account> accounts) {
        return accounts.stream().map(account -> account.getAmount().multiply(BigDecimal.valueOf(account.getCurrency().rate))
        .round(new MathContext(4,RoundingMode.HALF_UP))).reduce(BigDecimal.valueOf(0),BigDecimal::add);
    }

    /**
     * Zwraca imiona użytkowników w formie zbioru, którzy spełniają podany warunek.
     */
    public Set<String> getUsersForPredicate(final Predicate<User> userPredicate) {
        return holdings.stream().flatMap(holding -> holding.getCompanies().stream())
                .flatMap(company -> company.getUsers().stream()).filter(userPredicate)
                .map(user -> user.getFirstName()).collect(Collectors.toSet());
    }

    /**
     * Metoda filtruje użytkowników starszych niż podany jako parametr wiek, wyświetla ich na konsoli, odrzuca mężczyzn
     * i zwraca ich imiona w formie listy.
     */
    public List<String> getOldWoman(final int age) {
        return holdings.stream().flatMap(holding -> holding.getCompanies().stream())
                .flatMap(company -> company.getUsers().stream())
                .filter(user -> user.getAge() > age).peek(System.out::println)
                .filter(user -> user.getSex() == Sex.MAN)
                .map(user -> user.getFirstName()).collect(Collectors.toList());
    }

    /**
     * Dla każdej firmy uruchamia przekazaną metodę.
     */
    public void executeForEachCompany (final Consumer<Company> consumer) {
        throw new IllegalArgumentException();
    }

    /**
     * Wyszukuje najbogatsza kobietę i zwraca ją. Metoda musi uzwględniać to że rachunki są w różnych walutach.
     */
    public Optional<User> getRichestWoman() {
//        return holdings.stream().flatMap(holding -> holding.getCompanies().stream())
//                .flatMap(company -> company.getUsers().stream())
//                .filter(user -> user.getSex() == Sex.WOMAN)
//                .map(user -> user.getAccounts())
//                .max(Comparator.comparing(getUsers());
        return null;
    }

    /**
     * Zwraca nazwy pierwszych N firm. Kolejność nie ma znaczenia.
     */
    public Set<String> getFirstNCompany(final int n) {
        return holdings.stream().flatMap(holding -> holding.getCompanies().stream())
        .map(company -> company.getName()).limit(n).collect(Collectors.toSet());
    }

    /**
     * Metoda zwraca jaki rodzaj rachunku jest najpopularniejszy. Stwórz pomocniczą metodę getAccountStream.
     * Jeżeli nie udało się znaleźć najpopularniejszego rachunku metoda ma wyrzucić wyjątek IllegalStateException.
     * Pierwsza instrukcja metody to return.
     */
    public AccountType getMostPopularAccountType() {
        return null;
    }

    /**
     * Zwraca pierwszego z brzegu użytkownika dla podanego warunku. W przypadku kiedy nie znajdzie użytkownika wyrzuca
     * wyjątek IllegalArgumentException.
     */
    public User getUser(final Predicate<User> predicate) {
        return holdings.stream().flatMap(holding -> holding.getCompanies().stream())
                .flatMap(company -> company.getUsers().stream())
                .filter(predicate).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    /**
     * Zwraca mapę firm, gdzie kluczem jest jej nazwa a wartością lista pracowników.
     */
    public Map<String, List<User>> getUserPerCompany() {
        return null;
    }


    /**
     * Zwraca mapę firm, gdzie kluczem jest jej nazwa a wartością lista pracowników przechowywanych jako String
     * składający się z imienia i nazwiska. Podpowiedź:  Możesz skorzystać z metody entrySet.
     */
    public Map<String, List<String>> getUserPerCompanyAsString() {
        return null;
    }

    /**
     * Zwraca mapę firm, gdzie kluczem jest jej nazwa a wartością lista pracowników przechowywanych jako obiekty
     * typu T, tworzonych za pomocą przekazanej funkcji.
     */
    public <T> Map<String, List<T>> getUserPerCompany(final Function<User, T> converter) {
        return null;
    }

    /**
     * Zwraca mapę gdzie kluczem jest flaga mówiąca o tym czy mamy do czynienia z mężczyzną, czy z kobietą.
     * Osoby "innej" płci mają zostać zignorowane. Wartością jest natomiast zbiór nazwisk tych osób.
     */
    public Map<Boolean, Set<String>> getUserBySex() {
        return null;
    }

    /**
     * Zwraca mapę rachunków, gdzie kluczem jest numer rachunku, a wartością ten rachunek.
     */
    public Map<String, Account> createAccountsMap() {
        return null;
    }

    /**
     * Zwraca listę wszystkich imion w postaci Stringa, gdzie imiona oddzielone są spacją i nie zawierają powtórzeń.
     */
    public String getUserNames() {
        return null;
    }

    /**
     * Zwraca zbiór wszystkich użytkowników. Jeżeli jest ich więcej niż 10 to obcina ich ilość do 10.
     */
    public Set<User> getUsers() {
        return null;
    }

    /**
     * Zapisuje listę numerów rachunków w pliku na dysku, gdzie w każda linijka wygląda następująco:
     * NUMER_RACHUNKU|KWOTA|WALUTA
     * <p>
     * Skorzystaj z strumieni i try-resources.
     */
    public void saveAccountsInFile(final String fileName) {
        throw new IllegalArgumentException("not implemented yet");
    }

    /**
     * Zwraca użytkownika, który spełnia podany warunek.
     */
    public Optional<User> findUser(final Predicate<User> userPredicate) {
        return null;
    }

    /**
     * Dla podanego użytkownika zwraca informacje o tym ile ma lat w formie:
     * IMIE NAZWISKO ma lat X. Jeżeli użytkownik nie istnieje to zwraca text: Brak użytkownika.
     * <p>
     * Uwaga: W prawdziwym kodzie nie przekazuj Optionali jako parametrów.
     */
    public String getAdultantStatus(final Optional<User> user) {
        return null;
    }

    /**
     * Metoda wypisuje na ekranie wszystkich użytkowników (imię, nazwisko) posortowanych od z do a.
     * Zosia Psikuta, Zenon Kucowski, Zenek Jawowy ... Alfred Pasibrzuch, Adam Wojcik
     */
    public void showAllUser() {
        throw new IllegalArgumentException("not implemented yet");
    }

    /**
     * Zwraca mapę, gdzie kluczem jest typ rachunku a wartością kwota wszystkich środków na rachunkach tego typu
     * przeliczona na złotówki.
     */
    public Map<AccountType, BigDecimal> getMoneyOnAccounts() {
        return null;
    }

    /**
     * Zwraca sumę kwadratów wieków wszystkich użytkowników.
     */
    public int getAgeSquaresSum() {
        return -1;
    }

    /**
     * Metoda zwraca N losowych użytkowników (liczba jest stała). Skorzystaj z metody generate. Użytkownicy nie mogą się
     * powtarzać, wszystkie zmienną muszą być final. Jeżeli podano liczbę większą niż liczba użytkowników należy
     * wyrzucić wyjątek (bez zmiany sygnatury metody).
     */
    public List<User> getRandomUsers(final int n) {
        return null;
    }

    /**
     * Zwraca strumień wszystkich firm.
     */
    private Stream<Company> getCompanyStream() {
        return null;
    }

    /**
     * Zwraca zbiór walut w jakich są rachunki.
     */
    private Set<Currency> getCurenciesSet() {
        return null;
    }

    /**
     * Tworzy strumień rachunków.
     */
    private Stream<Account> getAccoutStream() {
        return null;
    }

    /**
     * Tworzy strumień użytkowników.
     */
    private Stream<User> getUserStream() {
        return null;
    }

}