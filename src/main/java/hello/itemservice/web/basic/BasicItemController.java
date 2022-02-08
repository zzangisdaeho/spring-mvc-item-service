package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                            @RequestParam int price,
                            @RequestParam Integer quantity,
                            Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);
        itemRepository.save(item);
        model.addAttribute("item", item);
        return "basic/item";
    }

    /**
     * @ModelAttribute("item") Item item
     * model.addAttribute("item", item); 자동 추가
     **/
//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item, Model model) {
        itemRepository.save(item);
        //model.addAttribute("item", item); //자동 추가, 생략 가능
        return "basic/item";
    }

    /**
     * @ModelAttribute name 생략 가능
     * model.addAttribute(item); 자동 추가, 생략 가능
     * 생략시 model에 저장되는 name은 클래스명 첫글자만 소문자로 등록 Item -> item */
//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    /**
     * @ModelAttribute 자체 생략 가능
     * model.addAttribute(item) 자동 추가 */
//    @PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    /**
     * PRG - Post/Redirect/Get
     * 새로고침시 브라우저에서 마지막 한 요청을 반복한다.
     * Post 요청으로 끝난 페이지는 다시 Post요청하여 데이터가 중복으로 쌓일 수 있다,
     * Redirect 사용시 브라우저는 302코드와 Header의 Location값을 통해 Location으로 Get요청을 보낸다.
     * 마지막 요청이 Get이므로 새로고침해도 중복 데이터가 쌓일 일이 없다.
     */
//    @PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);
//        return "redirect:/basic/items/" + item.getId();
        //  /로부터 시작하지 않으면 현재 경로 (/basic/items) 부터 시작한다 원래 /부터 시작하면 절대경로, 아니면 상대경로가 국룰이다.
        // 기존 경로가 /basic/items/add 였으니 add부분만 바뀐다고 보면 된다.
        return "redirect:" + item.getId();
    }

    /**
     * RedirectAttributes
     */
    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        // RedirectAttribute에 넣은 속성값 중 redirect에서 쓴 속성값{itemId}를 제외한 status는 queryParam으로 전송된다.
        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
        //  /로부터 시작하지 않으면 현재 경로 (/basic/items/{itemId}) 부터 시작한다 원래 /부터 시작하면 절대경로, 아니면 상대경로가 국룰이다.
        // 기존 경로가 /basic/items/{itemId}/edit 였으니 edit 바뀐다고 보면 된다.
        // 고로 아래코드로 보내면 /basic/items/1/1 같은 이상한 주소로 바뀌므로 쓰면 안된다.
//        return "redirect:{itemId}";
    }

    @PostConstruct
    public void init(){
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}
