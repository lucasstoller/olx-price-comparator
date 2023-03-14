package org.example;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main {
    public static void main(String[] args) {
        String searchQuery = "iphone" ;
        String baseUrl = "https://newyork.craigslist.org/" ;
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        try {
            String searchUrl = baseUrl + "search/sss?sort=rel&query=" + URLEncoder.encode(searchQuery, "UTF-8");
            HtmlPage page = client.getPage("https://www.olx.com.br/estado-sp/sao-paulo-e-regiao?q=ps5");

            List<HtmlElement> items = page.getByXPath("//ul[@id='ad-list']//li") ;
            if(items.isEmpty()){
                System.out.println("No items found !");
            }else{
                for(HtmlElement htmlItem : items){
                    try {
                        HtmlAnchor itemAnchor = htmlItem.getFirstByXPath(".//a");
                        List<HtmlElement> spans = htmlItem.getByXPath(".//span");
                        HtmlElement spanPrice = spans.stream().filter(span -> {
                            return span.getAttribute("aria-label").toLowerCase().contains("preço do item");
                        }).findFirst().orElse(null);

                        // It is possible that an item doesn't have any price, we set the price to 0.0 in this case
                        String itemPrice = spanPrice == null ? "0.0" : spanPrice.getFirstChild().getNodeValue();

                        Item item = new Item();
                        item.setTitle(itemAnchor.getAttributeDirect("title"));
                        item.setUrl(itemAnchor.getHrefAttribute());

                        item.setPrice(new BigDecimal(itemPrice.replace("R$", "").trim()));

                        ObjectMapper mapper = new ObjectMapper();
                        String jsonString = mapper.writeValueAsString(item) ;

                        System.out.println(jsonString);
                    } catch (NullPointerException err) {
                        System.out.println("ad");
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

    }
}

class Item {
    private String title ;
    private BigDecimal price ;
    private String url ;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }


}