package stm.ionis.technologiexml;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Controller
public class MainController2 {
//    @GetMapping("/")
    public String homePage() {
        return "home";
    }

//    @GetMapping("/all")
    public String allIDs(Model model) {
        // Nous récupérons une instance de factory qui se chargera de nous fournir
        // un parseur
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            //Méthode qui permet d'activer la vérification du fichier
            factory.setValidating(true);
            // Création de notre parseur via la factory
            DocumentBuilder builder = factory.newDocumentBuilder();
            //création de notre objet d'erreurs
            ErrorHandler errHandler = new SimpleErrorHandler();
            //Affectation de notre objet au document pour interception des erreurs éventuelles
            builder.setErrorHandler(errHandler);
            File[] filesXML = {
                    new File("src/main/resources/static/xml/carte01.xml"),
                    new File("src/main/resources/static/xml/carte02.xml")
            };

            ArrayList<Map> cartes = new ArrayList<Map>();;
            for (int i = 0; i < filesXML.length; i++) {
                // parsing de notre fichier via un objet File et récupération d'un
                // objet Document
                // Ce dernier représente la hiérarchie d'objet créée pendant le parsing
                Document xml = builder.parse(filesXML[i]);

                // Via notre objet Document, nous pouvons récupérer un objet Element
                // Ce dernier représente un élément XML mais, avec la méthode ci-dessous,
                // cet élément sera la racine du document
                Element root = xml.getDocumentElement();
                cartes.add(getCarteDescription(root));
            }
            model.addAttribute("cartes", cartes);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "all";
    }

//    @GetMapping("/one")
    public String oneID(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    /**
     * Méthode qui va parser le contenu d'un nœud
     * @param n
     * @return
     */
    public static Map<String, String> getCarteDescription(Node n){
        String str = new String();
        Map<String, String> map = new HashMap<String, String>();
        //Nous nous assurons que le nœud passé en paramètre est une instance d'Element
        //juste au cas où il s'agisse d'un texte ou d'un espace, etc.
        map.put("numero", n.getAttributes().item(0).getNodeValue());
        try {
            System.out.println(n.getChildNodes().getLength());
            System.out.println(n.getChildNodes().item(1).getNodeName());
            System.out.println(n.getChildNodes().item(0).getNodeValue());
        }catch (Exception e){
            e.printStackTrace();
        }

        map.put("photo", n.getChildNodes().item(1).getChildNodes().item(1).getAttributes().item(0).getNodeValue());

        for (int i = 0; i < n.getChildNodes().getLength(); i++) {
            if (n.getChildNodes().item(i) instanceof Element){
                for (int j = 0; j < n.getChildNodes().item(i).getChildNodes().getLength(); j++) {
                    if (n.getChildNodes().item(i).getChildNodes().item(j) instanceof Element) {
                        switch (n.getChildNodes().item(i).getChildNodes().item(j).getNodeName()){
                            case "nom":
                                map.put("nom", n.getChildNodes().item(i).getChildNodes().item(j).getTextContent());
                                break;
                            case "prenom":
                                map.put("prenom", n.getChildNodes().item(i).getChildNodes().item(j).getTextContent());
                                break;
                            case "sexe":
                                map.put("sexe", n.getChildNodes().item(i).getChildNodes().item(j).getAttributes().item(0).getNodeValue());
                                break;
                            case "naissance":
                                map.put("dateNaissance", n.getChildNodes().item(i).getChildNodes().item(j).getAttributes().item(0).getNodeValue());
                                map.put("lieuNaissance", n.getChildNodes().item(i).getChildNodes().item(j).getAttributes().item(1).getNodeValue());
                                break;
                            case "taille":
                                map.put("taille", n.getChildNodes().item(i).getChildNodes().item(j).getTextContent());
                                break;
                            case "signature":
                                map.put("signature", n.getChildNodes().item(i).getChildNodes().item(j).getAttributes().item(0).getNodeValue());
                                break;
                            case "adresse":
                                map.put("adresse", n.getChildNodes().item(i).getChildNodes().item(j).getTextContent());
                                break;
                            case "date":
                                map.put("dateDelivrance", n.getChildNodes().item(i).getChildNodes().item(j).getAttributes().item(0).getNodeValue());
                                map.put("dateExpiration", n.getChildNodes().item(i).getChildNodes().item(j).getAttributes().item(1).getNodeValue());
                                break;
                            case "par":
                                map.put("par", n.getChildNodes().item(i).getChildNodes().item(j).getTextContent());
                                break;
                        }
                    }
                }
            }
        }

        return map;
    }

}


