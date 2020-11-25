# UIMA - Html2Text - Text2Html

### Features

- Transforms HTML to Text (CAS)
    - Detects HTML entities by using `HtmlAnnotator`
    - Adds `ValueBetweenTagType` annotations to detect the values between HTML tags
    - Adds a `html2TextView` view with a plain text version of the HTML document
    - REST Controller which return the CAS file with all the annotations and views
    

- Transforms Text (CAS) to HTML (CAS)
    - This functionality is a little buggy due to UIMA limitations of the `AlignedString` class
    - Transforms a CAS with `ValueBetweenTagType` annotations back into a HTML output
    


### Build project:

    mvn clean install

When using in a docker-compose like in the dgfisma project, build it in docker

    docker-compose build uima

### Test locally

#### Html2Text

    POST http://localhost:8008/html2text
    
    {
        "text": "<p>Hello World</p>"
    }

#### Text2Html

    POST http://localhost:8008/text2html
    
    {
        "text": "XMI of a CAS. See below for an example."
    }



#### Example CAS to test 

    <?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<xmi:XMI xmlns:pos=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/lexmorph\/type\/pos.ecore\" xmlns:type=\"http:\/\/\/com\/crosslang\/uima\/type.ecore\" xmlns:tcas=\"http:\/\/\/uima\/tcas.ecore\" xmlns:html=\"http:\/\/\/com\/crosslang\/sdk\/types\/html.ecore\" xmlns:xmi=\"http:\/\/www.omg.org\/XMI\" xmlns:cas=\"http:\/\/\/uima\/cas.ecore\" xmlns:tweet=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/lexmorph\/type\/pos\/tweet.ecore\" xmlns:v22=\"http:\/\/\/com\/crosslang\/uima\/sdk\/type\/copied\/v2.ecore\" xmlns:segment=\"http:\/\/\/com\/crosslang\/sdk\/types\/classification\/bad\/segment.ecore\" xmlns:dependency=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/syntax\/type\/dependency.ecore\" xmlns:cassis=\"http:\/\/\/cassis.ecore\" xmlns:v2=\"http:\/\/\/com\/crosslang\/uima\/sdk\/type\/clgw\/v2.ecore\" xmlns:reordering=\"http:\/\/\/com\/crosslang\/sdk\/types\/ruta\/reordering.ecore\" xmlns:training=\"http:\/\/\/com\/crosslang\/sdk\/types\/split\/training.ecore\" xmlns:sub=\"http:\/\/\/com\/crosslang\/sdk\/types\/splitsentence\/sub.ecore\" xmlns:quotepos=\"http:\/\/\/com\/crosslang\/sdk\/types\/classification\/quotepos.ecore\" xmlns:type5=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/metadata\/type.ecore\" xmlns:splitsentence=\"http:\/\/\/com\/crosslang\/sdk\/types\/splitsentence.ecore\" xmlns:clgw=\"http:\/\/\/com\/crosslang\/uima\/sdk\/type\/clgw.ecore\" xmlns:type2=\"http:\/\/\/com\/crosslang\/uimahtmltotext\/uima\/type.ecore\" xmlns:verbcomplex=\"http:\/\/\/com\/crosslang\/sdk\/types\/classification\/verbcomplex.ecore\" xmlns:ruta=\"http:\/\/\/com\/crosslang\/sdk\/types\/ruta.ecore\" xmlns:token=\"http:\/\/\/com\/crosslang\/sdk\/types\/moses\/token.ecore\" xmlns:norm=\"http:\/\/\/com\/crosslang\/sdk\/types\/norm.ecore\" xmlns:clause=\"http:\/\/\/com\/crosslang\/sdk\/types\/classification\/clause.ecore\" xmlns:morph=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/lexmorph\/type\/morph.ecore\" xmlns:type4=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/frequency\/tfidf\/type.ecore\" xmlns:regex=\"http:\/\/\/com\/crosslang\/sdk\/types\/regex.ecore\" xmlns:type9=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/transform\/type.ecore\" xmlns:type3=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/anomaly\/type.ecore\" xmlns:type8=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/syntax\/type.ecore\" xmlns:type6=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/ner\/type.ecore\" xmlns:moses=\"http:\/\/\/com\/crosslang\/sdk\/types\/moses.ecore\" xmlns:type7=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/segmentation\/type.ecore\" xmlns:constituent=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/syntax\/type\/constituent.ecore\" xmlns:classification=\"http:\/\/\/com\/crosslang\/sdk\/types\/classification.ecore\" xmlns:chunk=\"http:\/\/\/de\/tudarmstadt\/ukp\/dkpro\/core\/api\/syntax\/type\/chunk.ecore\" xmlns:normalization=\"http:\/\/\/com\/crosslang\/sdk\/types\/ruta\/normalization.ecore\" xmi:version=\"2.0\">\r\n    <cas:NULL xmi:id=\"0\"\/>\r\n    <html:HtmlTag xmi:id=\"15\" sofa=\"1\" begin=\"0\" end=\"3\" tagName=\"p\" tagRole=\"OPENING\" attributes=\"\"\/>\r\n    <html:HtmlTag xmi:id=\"22\" sofa=\"1\" begin=\"14\" end=\"18\" tagName=\"p\" tagRole=\"CLOSING\"\/>\r\n    <type5:DocumentMetaData xmi:id=\"41\" sofa=\"1\" begin=\"0\" end=\"18\" language=\"en\" documentTitle=\"docTitle\" documentId=\"docId\" collectionId=\"colId\" isLastSegment=\"false\"\/>\r\n    <type2:ValueBetweenTagType xmi:id=\"52\" sofa=\"1\" begin=\"3\" end=\"14\" tagName=\"p\" attributes=\"\"\/>\r\n    <tcas:DocumentAnnotation xmi:id=\"36\" sofa=\"29\" begin=\"0\" end=\"11\" language=\"en\"\/>\r\n    <type2:ValueBetweenTagType xmi:id=\"59\" sofa=\"29\" begin=\"0\" end=\"11\" tagName=\"p\" attributes=\"\"\/>\r\n    <cas:Sofa xmi:id=\"1\" sofaNum=\"1\" sofaID=\"_InitialView\" mimeType=\"text\" sofaString=\"&lt;p&gt;Hello World&lt;\/p&gt;\"\/>\r\n    <cas:Sofa xmi:id=\"29\" sofaNum=\"2\" sofaID=\"html2textView\" mimeType=\"text\" sofaString=\"Hello World\"\/>\r\n    <cas:View sofa=\"1\" members=\"15 22 41 52\"\/>\r\n    <cas:View sofa=\"29\" members=\"36 59\"\/>\r\n<\/xmi:XMI>

#### Fetch typesystem

    GET http://localhost:8008/html2text/typesystem