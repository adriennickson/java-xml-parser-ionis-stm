package stm.ionis.technologiexml;

import com.lowagie.text.DocumentException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.w3c.dom.*;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;

@Controller
public class MainController {

    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    @GetMapping("/all")
    public String allIDs(Model model) {
        ArrayList<Carte> cartes = getCartes();
        model.addAttribute("cartes", cartes);
        return "all";
    }

    @GetMapping("/one")
    public String getOneByID(@RequestParam(name="id", required=false, defaultValue="880692310285") String id, Model model) {
        ArrayList<Carte> cartes = getCartes();
        Carte carte = new Carte(null);
        for (int i = 0; i < cartes.size(); i++) {
            if (cartes.get(i).getNumero().equals(id)){
                carte = cartes.get(i);
            }
        }
        model.addAttribute("carte", carte);
        return "editCard";
    }

    @PostMapping("/one")
    public RedirectView postOneByID(@ModelAttribute Carte carte) {
        saveCarte(carte);
        return new RedirectView("/all");
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getPDFByID(@RequestParam(name="id", required=false, defaultValue="880692310285") String id, Model model) {
        ArrayList<Carte> cartes = getCartes();
        Carte carte = new Carte(null);
        for (int i = 0; i < cartes.size(); i++) {
            if (cartes.get(i).getNumero().equals(id)){
                carte = cartes.get(i);
            }
        }

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("carte", carte);

        String html =  templateEngine.process("templates/pdf", context);

        String filename = "carte.pdf";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
        }catch (DocumentException e){
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        return response;

    }

    /**
     * Méthode qui va nous retourner une collection contenant les classes correspondanent à nos fichiers XML.
     * @return cartes
     */
    public static ArrayList<Carte> getCartes(){

        ArrayList<String> path = new ArrayList<String>(
                Arrays.asList(
                        "src/main/resources/static/xml/carte01.xml",
                        "src/main/resources/static/xml/carte02.xml"
                )
        );

        // Nous récupérons une instance de factory qui se chargera de nous fournir un parseur
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        ArrayList<Carte> cartes = new ArrayList<Carte>();;
        try {
            // Méthode qui permet d'activer la vérification du fichier
            factory.setValidating(true);
            factory.setIgnoringElementContentWhitespace(true); // très important!
            // Création de notre parseur via la factory
            DocumentBuilder builder = factory.newDocumentBuilder();
            // création de notre objet d'erreurs
            ErrorHandler errHandler = new SimpleErrorHandler();
            // Affectation de notre objet au document pour interception des erreurs éventuelles
            builder.setErrorHandler(errHandler);
            File[] filesXML = new File[path.size()];
            for (int i = 0; i < filesXML.length; i++) {
                filesXML[i]= new File( path.get(i));
            }

            for (int i = 0; i < filesXML.length; i++) {
                // parsing de notre fichier via un objet File et récupération d'un objet Document
                // Ce dernier représente la hiérarchie d'objet créée pendant le parsing
                Document xml = builder.parse(filesXML[i]);

                // Via notre objet Document, nous pouvons récupérer un objet Element
                // Ce dernier représente un élément XML mais, avec la méthode ci-dessous,
                // cet élément sera la racine du document
                Element root = xml.getDocumentElement();
                cartes.add(getCarteDescription(root));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cartes;
    }

    /**
     * Méthode qui va parser le contenu d'un nœud
     * @param n
     * @return
     */
    public static Carte getCarteDescription(Node n){
        //Nous nous assurons que le nœud passé en paramètre est une instance d'Element
        //juste au cas où il s'agisse d'un texte ou d'un espace, etc.
        Carte carte = new Carte(n.getAttributes().item(0).getNodeValue());
        carte.setPhoto(n.getChildNodes().item(0).getChildNodes().item(0).getAttributes().item(0).getNodeValue());
        carte.setNom(n.getChildNodes().item(0).getChildNodes().item(1).getTextContent());
        carte.setPrenom(n.getChildNodes().item(0).getChildNodes().item(2).getTextContent());
        carte.setSexe(n.getChildNodes().item(0).getChildNodes().item(3).getAttributes().item(0).getNodeValue().charAt(0));
        carte.setDateNaissance(n.getChildNodes().item(0).getChildNodes().item(4).getAttributes().item(0).getNodeValue());
        carte.setLieuNaissance(n.getChildNodes().item(0).getChildNodes().item(4).getAttributes().item(1).getNodeValue());
        carte.setTaille(n.getChildNodes().item(0).getChildNodes().item(5).getTextContent());
        carte.setSignature(n.getChildNodes().item(0).getChildNodes().item(6).getAttributes().item(0).getNodeValue());

        carte.setAdresse(n.getChildNodes().item(1).getChildNodes().item(0).getTextContent());
        carte.setDateDelivrance(n.getChildNodes().item(1).getChildNodes().item(1).getAttributes().item(0).getNodeValue());
        carte.setDateExpiration(n.getChildNodes().item(1).getChildNodes().item(1).getAttributes().item(1).getNodeValue());
        carte.setPar(n.getChildNodes().item(1).getChildNodes().item(2).getTextContent());

        return carte;
    }

    /**
     * Méthode qui va créer et sauvegarder le fichier XML a partir de l'instance de classe.
     * @param carte
     * @return
     */
    public static void saveCarte(Carte carte){

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            //la première chose qui change, nous n'allons pas lire un fichier
            //mais nous allons en créer un de toutes pièces
            Document xml = builder.newDocument();

            //Création de notre élément racine
            Element root = xml.createElement("carte");
            root.setAttribute("numero", carte.getNumero());

            //ensuite nous créons tous les nœuds de notre fichier XML
            Element recto = xml.createElement("recto");

            Element photo = xml.createElement("photo");
            photo.setAttribute("src", carte.getPhoto());

            Element nom = xml.createElement("nom");
            nom.setTextContent(carte.getNom());

            Element prenom = xml.createElement("prenom");
            prenom.setTextContent(carte.getPrenom());

            Element sexe = xml.createElement("sexe");
            sexe.setAttribute("valeur", carte.getSexe()+"");

            Element naissance = xml.createElement("naissance");
            naissance.setAttribute("date", carte.getDateNaissance());
            naissance.setAttribute("lieu", carte.getLieuNaissance());

            Element taille = xml.createElement("taille");
            taille.setTextContent(carte.getTaille());

            Element signature = xml.createElement("signature");
            signature.setAttribute("src", carte.getSignature());

            Element verso = xml.createElement("verso");

            Element adresse = xml.createElement("adresse");
            adresse.setTextContent(carte.getAdresse());

            Element date = xml.createElement("date");
            date.setAttribute("delivrance", carte.getDateDelivrance());
            date.setAttribute("expiration", carte.getDateExpiration());

            Element par = xml.createElement("par");
            par.setTextContent(carte.getPar());

            //Nous lions les nœuds les uns aux autres pour faire notre structure XML
            //nous ajoutons donc les nœuds "titre" aux nœuds "livre"
            recto.appendChild(photo);
            recto.appendChild(nom);
            recto.appendChild(prenom);
            recto.appendChild(sexe);
            recto.appendChild(naissance);
            recto.appendChild(taille);
            recto.appendChild(signature);

            verso.appendChild(adresse);
            verso.appendChild(date);
            verso.appendChild(par);
            //nous ajoutons donc les nœuds "recto" et "verso" au nœud "carte"
            root.appendChild(recto);
            root.appendChild(verso);

            //On crée un fichier xml correspondant au résultat
            //construire la transformation inactive
            Transformer t = TransformerFactory.newInstance().newTransformer();
            //définir les propriétés de sortie pour obtenir un nœud DOCTYPE
            t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "carte.dtd");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // Sauvegarder dans le bon fichier
            String resultFile = "src/main/resources/static/xml/carte";
            ArrayList<Carte> cartes = getCartes();
            for (int i = 0; i < cartes.size(); i++) {
                if (cartes.get(i).getNumero().equals(carte.getNumero())){
                    resultFile = resultFile + "0" + Integer.toString(Integer.sum(i,1)) + ".xml";
                }
            }

            StreamResult XML = new StreamResult(resultFile);

            t.transform(new DOMSource(root), XML);

        } catch (DOMException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return;
    }

}