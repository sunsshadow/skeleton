package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraints.NotEmpty;

import static java.lang.System.out;

@Path("/images")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptImageController {
    private final AnnotateImageRequest.Builder requestBuilder;

    public ReceiptImageController() {
        // DOCUMENT_TEXT_DETECTION is not the best or only OCR method available
        Feature ocrFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        this.requestBuilder = AnnotateImageRequest.newBuilder().addFeatures(ocrFeature);

    }

    /**
     * This borrows heavily from the Google Vision API Docs.  See:
     * https://cloud.google.com/vision/docs/detecting-fulltext
     *
     * YOU SHOULD MODIFY THIS METHOD TO RETURN A ReceiptSuggestionResponse:
     *
     * public class ReceiptSuggestionResponse {
     *     String merchantName;
     *     String amount;
     * }
     */
    @POST
    public ReceiptSuggestionResponse parseReceipt(final @NotEmpty String base64EncodedImage) throws Exception {
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);

            String merchantName = null;
            BigDecimal amount = null;

            // for a full line
//            final String wholeText = res.getTextAnnotationsList().get(0).getDescription();
//            final String[] lines = wholeText.split(System.getProperty("line.separator"));
//
//            out.printf("all lines : %s\n", wholeText);
//            for (int i = 0; i < lines.length; ++i) {
//                out.printf("line content : %s\n", lines[i]);
//                if (!NumberUtils.isCreatable(lines[i])) {
//                    merchantName = lines[i];
//                    break;
//                }
//            }
            int[] boundingBox = new int[4];
            if (res != null && res.getTextAnnotationsList() != null && res.getTextAnnotationsList().size()>0) {
                BoundingPoly all = res.getTextAnnotationsList().get(0).getBoundingPoly();
                out.printf("all descr : %s\n", res.getTextAnnotationsList().get(0).getDescription());
                boundingBox[0] = all.getVertices(0).getX(); // x
                boundingBox[1] = all.getVertices(0).getY(); // y
                boundingBox[2] = all.getVertices(1).getX() - all.getVertices(0).getX(); // width
                boundingBox[3] = all.getVertices(2).getY() - all.getVertices(0).getY(); // height


                for (int i = 1; i < res.getTextAnnotationsList().size(); ++i) {
                    String possibleMerchant = res.getTextAnnotationsList().get(i).getDescription();
                    if (!NumberUtils.isCreatable(possibleMerchant)) {
                        merchantName = possibleMerchant;
                        break;
                    }
                }

                for (int i = res.getTextAnnotationsList().size() - 1; i > 0; --i) {
                    String possibleAmount = res.getTextAnnotationsList().get(i).getDescription();

                    String numericPossibleAmount = possibleAmount.replaceAll("[$,]", "");
                    if (NumberUtils.isCreatable(numericPossibleAmount)) {
                        amount = new BigDecimal(numericPossibleAmount);
                        break;
                    }
                }
            }

            //TextAnnotation fullTextAnnotation = res.getFullTextAnnotation();
            return new ReceiptSuggestionResponse(merchantName, amount, boundingBox);
        }
    }
}
