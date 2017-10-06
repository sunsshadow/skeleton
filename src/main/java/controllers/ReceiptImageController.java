package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
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
        //System.out.print("receipt.base64EncodedImage " + base64EncodedImage.toString() + "hiiiii");
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);

            String merchantName = null;
            BigDecimal amount = null;

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
            BoundingPoly all = res.getTextAnnotationsList().get(0).getBoundingPoly();
            //res.getCropHintsAnnotation().
            out.printf("all Position : %s\n", all);
            out.printf("all descr : %s\n", res.getTextAnnotationsList().get(0).getDescription());
            boundingBox[0] = all.getVertices(0).getX(); // x
            boundingBox[1] = all.getVertices(0).getY(); // y
            boundingBox[2] = all.getVertices(1).getX() - all.getVertices(0).getX(); // width
            boundingBox[3] = all.getVertices(2).getY() - all.getVertices(0).getY(); // height


            for (int i = 1; i < res.getTextAnnotationsList().size(); ++i) {
                String possibleMerchant = res.getTextAnnotationsList().get(i).getDescription();
                BoundingPoly possibleMerchantPoly = res.getTextAnnotationsList().get(0).getBoundingPoly();
                out.printf("possibleMerchant: %s\n", possibleMerchant);
                out.printf("possibleMerchant Position : %s\n", possibleMerchantPoly);
                out.printf("StringUtils.isNumeric(numericPossibleAmount): %s\n", !NumberUtils.isCreatable(possibleMerchant));
                if (!NumberUtils.isCreatable(possibleMerchant)) {
                    merchantName = possibleMerchant;
                    break;
                }
            }

            for (int i = res.getTextAnnotationsList().size()-1; i > 0; --i) {
                String possibleAmount = res.getTextAnnotationsList().get(i).getDescription();
                out.printf("Text: %s\n", possibleAmount);

                String numericPossibleAmount = possibleAmount.replaceAll("[$,]", "");
                out.printf("numericPossibleAmount: %s\n", numericPossibleAmount);

                out.printf("StringUtils.isNumeric(numericPossibleAmount): %s\n", NumberUtils.isCreatable(numericPossibleAmount));
                if (NumberUtils.isCreatable(numericPossibleAmount)) {
                    amount = new BigDecimal(numericPossibleAmount);
                    break;
                }
            }


            //Image imageCropped = img.([vects[0].x, vects[0].y,
            //        vects[2].x - 1, vects[2].y - 1])
            // Your Algo Here!!
            // Sort text annotations by bounding polygon.  Top-most non-decimal text is the merchant
            // bottom-most decimal text is the total amount
//            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
//                out.printf("Position : %s\n", annotation.getBoundingPoly());
//                if (merchantName == null) {
//
//                }
//                out.printf("Text: %s\n", annotation.getDescription());
//            }

            //TextAnnotation fullTextAnnotation = res.getFullTextAnnotation();
            return new ReceiptSuggestionResponse(merchantName, amount, boundingBox);
        }
    }
}
