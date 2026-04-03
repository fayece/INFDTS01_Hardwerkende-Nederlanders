package nl.hardwerkendenederlanders.hrcms.Forms;

import lombok.Getter;
import lombok.Setter;

public class ArticleForm {
    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private  String textContent;

    @Getter
    @Setter
    private String publicationStatus;
}
