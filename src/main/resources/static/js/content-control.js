const stompClient = new StompJs.Client({brokerURL: 'ws://localhost:8080/websocket'});

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame)
    stompClient.subscribe('/send/content', (content) => {
        setupContent(content.body);
    });
    stompClient.subscribe('/send/server', (message) => {
        readMessage(message.body);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function readMessage(message){
    let page = document.getElementById('pageNum').textContent;
    if(message.length > 1){
        console.log(message)
        const serverInformation = JSON.parse(message);
        let currentPage = serverInformation.currentPage;
        let totalPages = serverInformation.totalPages;
        let content = serverInformation.content;

        if((page - 1) === currentPage) {
            $('#array-structure').html('');
            content.forEach((onAdded) => {
                console.log(currentPage)
                $('#array-structure').append(
                    '                     <div class="media-object stack-for-small" id="element_' + onAdded.id + '" style="border: solid 1px lightgray; padding: 10px; background-color: whitesmoke">\n' +
                    '                        <div class="media-object-section medium-6">\n' +
                    '                            <video class="thumbnail" width="300" height="200"  muted controls="controls">\n' +
                    '                                <source src="' + onAdded.fullPath + '" type="video/mp4" />\n' +
                    '                            </video>\n' +
                    '                        </div>\n' +
                    '                        <div class="media-object-section" style="position: relative">\n' +
                    '                            <div class="medium-12">\n' +
                    '                                <ul class="spisok">\n' +
                    '                                    <li style="font-size: 14px">Имя: <span>' + onAdded.fileName + '</span></li>\n' +
                    '                                    <li style="font-size: 14px">Дата: <span>' + onAdded.placedAt + '</span></li> <!-- исправить на дату записи видео в базе -->\n' +
                    '                                    <li style="font-size: 14px">Размер файла: <span>' + onAdded.fileSize + '</span></li> <!-- Размер сделать в гигабайтах -->\n' +
                    '                                </ul>\n' +
                    '                            </div>\n' +
                    '                            <div class="small-12 row button-group" style="position: absolute; bottom: 10px">\n' +
                    '                                <div class="small-6 column">\n' +
                    '                                    <form method="POST" action="/admin/files/get/photo/' + onAdded.id + '">\n' +
                    '                                        <input type="submit" value="Фото" class="submit small button disabled">\n' +
                    '                                    </form>\n' +
                    '                                </div>\n' +
                    '                                <div class="small-6 column">\n' +
                    '                                    <form method="POST" action="/admin/files/delete/' + onAdded.id + '">\n' +
                    '                                        <input class="alert small button" type="submit" value="Удалить">\n' +
                    '                                    </form>\n' +
                    '                                </div>\n' +
                    '                            </div>\n' +
                    '                        </div>\n' +
                    '                    </div>'
                )
            })
        }
    }else{
        let page = document.getElementById('pageNum').textContent;
        stompClient.publish({
            destination: "/app/answer",
            body: JSON.stringify(page)
        })
    }
}

function setupContent(content){
    let json = content;
    const message = JSON.parse(json);

    let totalPages = message.totalPages
    let currentPage = message.currentPage
    let oldPage = message.oldPage
    let onAdded = message.onAdd
    let onDeleted = message.onDelete

    console.log(oldPage + ' ' + currentPage)

    if(oldPage !== currentPage){
        window.location.replace('/admin/files/' + currentPage)
    }else{
        if(onDeleted !== null){
            document.getElementById('element_' + onDeleted.id).remove()
        }
        if(onAdded !== null){
            $('#array-structure').prepend(
                '                     <div class="media-object stack-for-small" id="element_' + onAdded.id + '" style="border: solid 1px lightgray; padding: 10px; background-color: whitesmoke">\n' +
                '                        <div class="media-object-section medium-6">\n' +
                '                            <video class="thumbnail" width="300" height="200"  muted controls="controls">\n' +
                '                                <source src="' + onAdded.fullPath + '" type="video/mp4" />\n' +
                '                            </video>\n' +
                '                        </div>\n' +
                '                        <div class="media-object-section" style="position: relative">\n' +
                '                            <div class="medium-12">\n' +
                '                                <ul class="spisok">\n' +
                '                                    <li style="font-size: 14px">Имя: <span>' + onAdded.fileName + '</span></li>\n' +
                '                                    <li style="font-size: 14px">Дата: <span>' + onAdded.placedAt + '</span></li> <!-- исправить на дату записи видео в базе -->\n' +
                '                                    <li style="font-size: 14px">Размер файла: <span>' + onAdded.fileSize + '</span></li> <!-- Размер сделать в гигабайтах -->\n' +
                '                                </ul>\n' +
                '                            </div>\n' +
                '                            <div class="small-12 row button-group" style="position: absolute; bottom: 10px">\n' +
                '                                <div class="small-6 column">\n' +
                '                                    <form method="POST" action="/admin/files/get/photo/' + onAdded.id + '">\n' +
                '                                        <input type="submit" value="Фото" class="submit small button disabled">\n' +
                '                                    </form>\n' +
                '                                </div>\n' +
                '                                <div class="small-6 column">\n' +
                '                                    <form method="POST" action="/admin/files/delete/' + onAdded.id + '">\n' +
                //'                                        <input type="hidden" name="' + [[${_csrf.parameterName}]] + '" value="' + [[${_csrf.token}]] + '">' +
                '                                        <input class="alert small button" type="submit" value="Удалить">\n' +
                '                                    </form>\n' +
                '                                </div>\n' +
                '                            </div>\n' +
                '                        </div>\n' +
                '                    </div>'
            )
        }
    }

    /*let json = content;
    const message = JSON.parse(json);
    let status = message.status;
    let onAdded = message.content;

    if(status === 1){ // added
        $('#array-structure').prepend(
            '                     <div class="media-object stack-for-small" id="element_' + onAdded.id + '" style="border: solid 1px lightgray; padding: 10px; background-color: whitesmoke">\n' +
            '                        <div class="media-object-section medium-6">\n' +
            '                            <video class="thumbnail" width="300" height="200"  muted controls="controls">\n' +
            '                                <source src="' + onAdded.fullPath + '" type="video/mp4" />\n' +
            '                            </video>\n' +
            '                        </div>\n' +
            '                        <div class="media-object-section" style="position: relative">\n' +
            '                            <div class="medium-12">\n' +
            '                                <ul class="spisok">\n' +
            '                                    <li style="font-size: 14px">Имя: <span>' + onAdded.fileName + '</span></li>\n' +
            '                                    <li style="font-size: 14px">Дата: <span>' + onAdded.placedAt + '</span></li> <!-- исправить на дату записи видео в базе -->\n' +
            '                                    <li style="font-size: 14px">Размер файла: <span>' + onAdded.fileSize + '</span></li> <!-- Размер сделать в гигабайтах -->\n' +
            '                                </ul>\n' +
            '                            </div>\n' +
            '                            <div class="small-12 row button-group" style="position: absolute; bottom: 10px">\n' +
            '                                <div class="small-6 column">\n' +
            '                                    <form method="POST" action="/admin/files/get/photo/' + onAdded.id + '">\n' +
            '                                        <input type="submit" value="Фото" class="submit small button disabled">\n' +
            '                                    </form>\n' +
            '                                </div>\n' +
            '                                <div class="small-6 column">\n' +
            '                                    <form method="POST" action="/admin/files/delete/' + onAdded.id + '">\n' +
          //'                                        <input type="hidden" name="' + [[${_csrf.parameterName}]] + '" value="' + [[${_csrf.token}]] + '">' +
            '                                        <input class="alert small button" type="submit" value="Удалить">\n' +
            '                                    </form>\n' +
            '                                </div>\n' +
            '                            </div>\n' +
            '                        </div>\n' +
            '                    </div>'
        )
    }else if(status === 2){ // deleted
        document.getElementById('element_' + onAdded.id).remove()
    }*/
}

stompClient.activate();

function deleteElement(id,page){
    console.log(id + ' ' + page)
    stompClient.publish({
        destination: "/app/delete-element",
        body: JSON.stringify({'id': id,'page':page})
    })
}

/*setInterval(update,1000)

function update(){
    stompClient.publish({
        destination: "/app/connection"
    });
    /!*stompClient.deactivate();*!/
}*/
