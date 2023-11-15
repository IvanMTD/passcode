const stompClient = new StompJs.Client({brokerURL: 'ws://localhost:8080/websocket'});

stompClient.onConnect = (frame) => {
    console.log('Conected: ' + frame)
    stompClient.subscribe('/send/content', (content) => {
        setupContents(content.body);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setupContents(content){
    if(content.length !== 0){
        let json = content;
        const array = JSON.parse(json);
        console.log(array)
    }else{
        console.log('nothing')
    }
    /*let json = contents;
    const newContent = JSON.parse(json);
    const fileInForm = "[[${fileInform}]]";
    for(let i=0; i<newContent.length; i++){
        if(fileInForm.id !== newContent){
            $("#uploadFiles").add(
                "<div className=\"media-object stack-for-small\" id=\"uploadFiles\"\n" +
                "                 style=\"border: solid 1px lightgray; padding: 10px; background-color: whitesmoke\">\n" +
                "                <div className=\"media-object-section\">\n" +
                "                    <!--<video controls width=\"300\" muted class=\"thumbnail\">\n" +
                "                        <source th:src=\"'/saved/' + ${f.getFileName()}\" type=\"video/mp4\"/>\n" +
                "                    </video>-->\n" +
                "                    <video className=\"thumbnail\" width=\"300\" height=\"200\" muted controls=\"controls\">\n" +
                "                        <source src=\"" + newContent[i].fullPath + "  \" type=\"video/mp4\"/>\n" +
                "                    </video>\n" +
                "                </div>\n" +
                "                <div className=\"media-object-section\" style=\"position: relative\">\n" +
                "                    <div className=\"medium-12\">\n" +
                "                        <ul className=\"spisok\">\n" +
                "                            <li style=\"font-size: 14px\">Имя: <span th:text=\"${f.getFileName()}\"></span></li>\n" +
                "                            <li style=\"font-size: 14px\">Дата: <span th:text=\"${f.getPlacedAt()}\"></span></li>\n" +
                "                            <!-- исправить на дату записи видео в базе -->\n" +
                "                            <li style=\"font-size: 14px\">Размер файла: <span th:text=\"${f.getFileSize()}\"></span></li>\n" +
                "                            <!-- Размер сделать в гигабайтах -->\n" +
                "                        </ul>\n" +
                "                    </div>\n" +
                "                    <div className=\"small-12 row button-group\" style=\"position: absolute; bottom: 10px\">\n" +
                "                        <div className=\"small-6 column\">\n" +
                "                            <form th:method=\"POST\" th:action=\"@{/admin/files/get/photo/{id}(id=${f.getId()})}\"\n" +
                "                                  th:object=\"${f}\">\n" +
                "                                <input type=\"submit\" value=\"Фото\" className=\"submit small button disabled\"\n" +
                "                                       th:if=\"${f.getImages().size()} == 0\">\n" +
                "                                    <input type=\"submit\" value=\"Фото\" className=\"submit small button\"\n" +
                "                                           th:if=\"${f.getImages().size()} != 0\">\n" +
                "                            </form>\n" +
                "                        </div>\n" +
                "                        <div className=\"small-6 column\">\n" +
                "                            <form th:method=\"POST\" th:action=\"@{/admin/files/delete/{id}(id=${f.getId()})}\">\n" +
                "                                <input className=\"alert small button\" type=\"submit\" value=\"Удалить\">\n" +
                "                            </form>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>"
            )
        }
    }*/
}

stompClient.activate();

/*setInterval(update,1000)

function update(){
    stompClient.publish({
        destination: "/app/connection"
    });
    /!*stompClient.deactivate();*!/
}*/
