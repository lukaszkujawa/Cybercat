package net.systemsarchitect.cybrcat.module.http;

import net.systemsarchitect.cybrcat.core.Module;
import net.systemsarchitect.cybrcat.core.anno.AllowMap;
import net.systemsarchitect.cybrcat.core.types.CCatValue;
import net.systemsarchitect.cybrcat.core.types.CCatValueMap;
import net.systemsarchitect.cybrcat.core.types.CCatValueString;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lukasz on 04/02/2017.
 */
public class Http extends Module {

    CloseableHttpClient httpclient;

    Map<String,CCatValue> params = new HashMap<>();
    Map<String,CCatValue> headers = new HashMap<>();

    BasicCookieStore sharedCookieStore;

    CCatValueString url;
    CCatValueString method = new CCatValueString("get");

    public Http() {
        sharedCookieStore = new BasicCookieStore();

        httpclient = HttpClients.custom()
                .setDefaultCookieStore(sharedCookieStore)
                .build();
    }

    @AllowMap
    public void setParams(CCatValue params) {
        this.params = new HashMap<>();
        for(Map.Entry<String,CCatValue> row : ((CCatValueMap)params).getValue().entrySet()) {
            this.params.put(row.getKey(), row.getValue());
        }
    }

    @AllowMap
    public void setHeaders(CCatValue headers) {
        for(Map.Entry<String,CCatValue> row : ((CCatValueMap)headers).getValue().entrySet()) {
            this.headers.put(row.getKey(), row.getValue());
        }
    }

    String getContent(CloseableHttpResponse r) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(r.getEntity().getContent(), writer);
        return writer.toString();
    }

    RequestBuilder getRequestBuilder(String url) throws URISyntaxException {
        if(method.getValue().equals("post")) {
            return RequestBuilder.post().setUri(new URI(url));
        }
        else if(method.getValue().equals("get")) {
            return RequestBuilder.get().setUri(new URI(url));
        }
        else {
            return null;
        }
    }

    void setRequestHeaders(RequestBuilder requestBuilder) {
        for(Map.Entry<String, CCatValue> row : headers.entrySet()) {
            requestBuilder.addHeader(row.getKey(), row.getValue().toString());
        }
    }

    void setRequestParams(RequestBuilder requestBuilder) {
        for(Map.Entry<String, CCatValue> row : params.entrySet()) {
            requestBuilder.addParameter(row.getKey(), row.getValue().toString());
        }
    }

    @Override
    public List<CCatValue> call(CCatValue _input) {

        try {

            CCatValueMap input = (CCatValueMap) _input;
            RequestBuilder requestBuilder = getRequestBuilder(input.get("url").toString());

            setRequestHeaders(requestBuilder);
            setRequestParams(requestBuilder);

            CloseableHttpResponse response = httpclient.execute(requestBuilder.build());
            String content = getContent(response);
            response.close();
            List<CCatValue> out = new ArrayList<>();
            out.add(new CCatValueString(content));

            return out;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
