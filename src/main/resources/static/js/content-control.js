const stompClient = new StompJs.Client({brokerURL: 'ws://localhost:8080/websocket'});
let contentTemp;

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame)
    stompClient.subscribe('/send/content', (content) => {
        setupContent(content.body);
    });
    stompClient.subscribe('/send/server', (message) => {
        readMessage(message.body);
    });
    stompClient.subscribe('/send/photo/check', (message) => {
        photoButtonUpdate(message.body);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

stompClient.activate();

function photoButtonUpdate(message){
    const parseMessage = JSON.parse(message)
    let content = parseMessage.content
    contentTemp = content;

    for(let i=0; i<content.length; i++){
        let elementId = content[i].id
        let pageElement = document.getElementById('photo_' + elementId)
        if(pageElement !== null){
            let images = content[i].images
            if(images.length !== 0){
                $('#photo_' + elementId).html('')
                $('#photo_' + elementId).append(
                    '<button name="' + elementId + '" class="submit small button" onclick="showPhotos(' + elementId + ')">Фото</button>'
                )
            }else{
                $('#photo_' + elementId).html('')
                $('#photo_' + elementId).append(
                    '<button class="submit small button disabled">Фото</button>'
                )
            }
        }
    }
}

function readMessage(message){
    let page = document.getElementById('pageNum').textContent;
    if(message.length > 1){
        const serverInformation = JSON.parse(message);
        let currentPage = serverInformation.currentPage;
        let totalPages = serverInformation.totalPages;
        let content = serverInformation.content;

        if((page - 1) === currentPage) {
            $('#array-structure').html('');
            content.forEach((onAdded) => {
                $('#array-structure').append(
                    '                     <div class="media-object stack-for-small" id="element_' + onAdded.id + '" style="border: solid 1px lightgray; padding: 10px; background-color: whitesmoke">\n' +
                    '                        <div class="media-object-section medium-6">\n' +
/*                  '                            <video class="thumbnail" width="300" height="200"  muted controls="controls">\n' +
                    '                                <source src="' + onAdded.fullPath + '" type="video/mp4" />\n' +
                    '                            </video>\n' +*/
                    '                            <img class="thumbnail" src="https://placehold.it/200x150">' +
                    '                        </div>\n' +
                    '                        <div class="media-object-section" style="position: relative">\n' +
                    '                            <div class="medium-12">\n' +
                    '                                <ul class="spisok">\n' +
                    '                                    <li style="font-size: 14px">ID: <span>' + onAdded.id + '</span></li>\n' +
                    '                                    <li style="font-size: 14px">Имя: <span>' + onAdded.fileName + '</span></li>\n' +
                    '                                    <li style="font-size: 14px">Дата: <span>' + onAdded.placedAt + '</span></li> <!-- исправить на дату записи видео в базе -->\n' +
                    '                                    <li style="font-size: 14px">Размер файла: <span>' + onAdded.fileSize + '</span></li> <!-- Размер сделать в гигабайтах -->\n' +
                    '                                </ul>\n' +
                    '                            </div>\n' +
                    '                            <div class="small-12 row button-group" style="position: absolute; bottom: 10px">\n' +
                    '                                <div id="photo_' + onAdded.id + '">' +
                    '                                    <button class="submit small button disabled">Фото</button>' +
                    '                                </div>' +
                    '                                <div class="small-6 column">\n' +
                    '                                    <button class="alert small button" onClick="deleteElement(' + onAdded.id + ',' + currentPage + ')">Удалить</button> ' +
                    '                                </div>\n' +
                    '                            </div>\n' +
                    '                        </div>\n' +
                    '                    </div>'
                )
                let images = onAdded.images
                $('#photo_' + onAdded.id).html('')
                if(images.length !== 0){
                    $('#photo_' + onAdded.id).prepend(
                        '<button name="' + onAdded.id + '" class="submit small button" onclick="showPhotos(' + onAdded.id + ')">Фото</button>'
                    )
                }else{
                    $('#photo_' + onAdded.id).prepend(
                        '<button class="submit small button disabled">Фото</button>'
                    )
                }
            })
            $('#navbar').html('')
            let cp = currentPage + 1
            let tp = totalPages;
            if(cp === 1){
                if(cp === tp){
                    $('#navbar').append(
                        '   <ul class="pagination text-center" role="navigation" aria-label="Pagination">\n' +
                        '       <li class="disabled">Previous <span class="show-for-sr">page</span></li>\n' +
                        '       <li class="current"><span class="show-for-sr">You\'re on page</span><span id="pageNum">1</span></li>\n' +
                        '       <li class="disabled">Next<span class="show-for-sr">page</span></li>\n' +
                        '   </ul>'
                    )
                }else{
                    $('#navbar').append(
                        '   <ul class="pagination text-center" role="navigation" aria-label="Pagination">\n' +
                        '       <li class="disabled">Previous <span class="show-for-sr">page</span></li>\n' +
                        '       <li class="current"><span class="show-for-sr">You\'re on page</span><span id="pageNum">1</span></li>\n' +
                        '       <li><a href="/admin/files/' + (currentPage + 1) + '">2</a></li>\n' +
                        '       <li><a href="/admin/files/' + (currentPage + 1) + '" aria-label="Next page">Next<span class="show-for-sr">page</span></a></li>\n' +
                        '   </ul>'
                    )
                }
            }else if(cp === tp){
                $('#navbar').append(
                    '   <ul class="pagination text-center" role="navigation" aria-label="Pagination">\n' +
                    '       <li><a href="/admin/files/' + (currentPage - 1) + '">Previous</a><span class="show-for-sr">page</span></li>\n' +
                    '       <li><a href="/admin/files/' + (currentPage - 1) + '">' + (cp - 1) + '</a></li>\n' +
                    '       <li class="current"><span class="show-for-sr">You\'re on page</span><span id="pageNum">' + cp + '</span></li>\n' +
                    '       <li class="disabled">Next<span class="show-for-sr">page</span></li>\n' +
                    '   </ul>'
                )
            }else{
                $('#navbar').append(
                    '   <ul class="pagination text-center" role="navigation" aria-label="Pagination">\n' +
                    '       <li><a href="/admin/files/' + (currentPage - 1) + '">Previous</a><span class="show-for-sr">page</span></li>\n' +
                    '       <li><a href="/admin/files/' + (currentPage - 1) + '">' + (cp - 1) + '</a></li>\n' +
                    '       <li class="current"><span class="show-for-sr">You\'re on page</span><span id="pageNum">' + cp + '</span></li>\n' +
                    '       <li><a href="/admin/files/' + (currentPage + 1) + '">' + (cp + 1) + '</a></li>\n' +
                    '       <li><a href="/admin/files/' + (currentPage + 1) + '" aria-label="Next page">Next<span class="show-for-sr">page</span></a></li>\n' +
                    '   </ul>'
                )
            }
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
    let pageNum = (document.getElementById('pageNum').textContent - 1);
    let json = content;
    const message = JSON.parse(json);

    let totalPage = message.totalPage
    let currentPage = message.currentPage
    let oldPage = message.oldPage
    let onAdded = message.onAdd
    let onDeleted = message.onDelete

    if(pageNum > oldPage){
        if((pageNum + 1) <= totalPage){
            window.location.replace('/admin/files/' + pageNum)
        }else{
            window.location.replace('/admin/files/' + (pageNum -1))
        }
    }else{
        if(oldPage !== currentPage){
            window.location.replace('/admin/files/' + currentPage)
        }else{
            if(onDeleted !== null){
                document.getElementById('element_' + onDeleted.id).remove()
            }
            if(onAdded !== null){
                $('#array-structure').prepend(
                    '<div class="media-object stack-for-small" id="element_' + onAdded.id + '" style="border: solid 1px lightgray; padding: 10px; background-color: whitesmoke">\n' +
                    '   <div class="media-object-section medium-6">\n' +
                    '       <img class="thumbnail" src="https://placehold.it/200x150">' +
                    '   </div>\n' +
                    '   <div class="media-object-section" style="position: relative">\n' +
                    '       <div class="medium-12">\n' +
                    '           <ul class="spisok">\n' +
                    '               <li style="font-size: 14px">ID: <span>' + onAdded.id + '</span></li>\n' +
                    '               <li style="font-size: 14px">Имя: <span>' + onAdded.fileName + '</span></li>\n' +
                    '               <li style="font-size: 14px">Дата: <span>' + onAdded.placedAt + '</span></li> <!-- исправить на дату записи видео в базе -->\n' +
                    '               <li style="font-size: 14px">Размер файла: <span>' + onAdded.fileSize + '</span></li> <!-- Размер сделать в гигабайтах -->\n' +
                    '           </ul>\n' +
                    '       </div>\n' +
                    '       <div class="small-12 row button-group" style="position: absolute; bottom: 10px">\n' +
                    '           <div class="small-6 column">\n' +
                    '               <div id="photo_' + onAdded.id + '">' +
                    '                   <button class="submit small button disabled">Фото</button>' +
                    '               </div>' +
                    '           </div>\n' +
                    '           <div class="small-6 column">\n' +
                    '               <button class="alert small button" onClick="deleteElement(' + onAdded.id + ',' + currentPage + ')">Удалить</button> ' +
                    '           </div>\n' +
                    '       </div>\n' +
                    '   </div>\n' +
                    '</div>'
                )

                let images = onAdded.images
                $('#photo_' + onAdded.id).html('')
                if(images.length !== 0){
                    $('#photo_' + onAdded.id).prepend(
                        '<button name="' + onAdded.id + '" class="submit small button" onclick="showPhotos(' + onAdded.id + ')">Фото</button>'
                    )
                }else{
                    $('#photo_' + onAdded.id).prepend(
                        '<button class="submit small button disabled">Фото</button>'
                    )
                }

                $('#navbar').html('')
                let cp = currentPage + 1
                let tp = totalPage;
                if(cp === 1){
                    if(cp === tp){
                        $('#navbar').append(
                            '   <ul class="pagination text-center" role="navigation" aria-label="Pagination">\n' +
                            '       <li class="disabled">Previous <span class="show-for-sr">page</span></li>\n' +
                            '       <li class="current"><span class="show-for-sr">You\'re on page</span><span id="pageNum">1</span></li>\n' +
                            '       <li class="disabled">Next<span class="show-for-sr">page</span></li>\n' +
                            '   </ul>'
                        )
                    }else{
                        $('#navbar').append(
                            '   <ul class="pagination text-center" role="navigation" aria-label="Pagination">\n' +
                            '       <li class="disabled">Previous <span class="show-for-sr">page</span></li>\n' +
                            '       <li class="current"><span class="show-for-sr">You\'re on page</span><span id="pageNum">1</span></li>\n' +
                            '       <li><a href="/admin/files/' + (currentPage + 1) + '">2</a></li>\n' +
                            '       <li><a href="/admin/files/' + (currentPage + 1) + '" aria-label="Next page">Next<span class="show-for-sr">page</span></a></li>\n' +
                            '   </ul>'
                        )
                    }
                }else if(cp === tp){
                    $('#navbar').append(
                        '   <ul class="pagination text-center" role="navigation" aria-label="Pagination">\n' +
                        '       <li><a href="/admin/files/' + (currentPage - 1) + '">Previous</a><span class="show-for-sr">page</span></li>\n' +
                        '       <li><a href="/admin/files/' + (currentPage - 1) + '">' + (cp - 1) + '</a></li>\n' +
                        '       <li class="current"><span class="show-for-sr">You\'re on page</span><span id="pageNum">' + cp + '</span></li>\n' +
                        '       <li class="disabled">Next<span class="show-for-sr">page</span></li>\n' +
                        '   </ul>'
                    )
                }else{
                    $('#navbar').append(
                        '   <ul class="pagination text-center" role="navigation" aria-label="Pagination">\n' +
                        '       <li><a href="/admin/files/' + (currentPage - 1) + '">Previous</a><span class="show-for-sr">page</span></li>\n' +
                        '       <li><a href="/admin/files/' + (currentPage - 1) + '">' + (cp - 1) + '</a></li>\n' +
                        '       <li class="current"><span class="show-for-sr">You\'re on page</span><span id="pageNum">' + cp + '</span></li>\n' +
                        '       <li><a href="/admin/files/' + (currentPage + 1) + '">' + (cp + 1) + '</a></li>\n' +
                        '       <li><a href="/admin/files/' + (currentPage + 1) + '" aria-label="Next page">Next<span class="show-for-sr">page</span></a></li>\n' +
                        '   </ul>'
                    )
                }
            }
        }
    }
}

function deleteElement(id,page){
    stompClient.publish({
        destination: "/app/delete-element",
        body: JSON.stringify({'id': id,'page':page})
    })
}

function setupPhotos(contentList,id){
    let content = null;

    if(typeof contentTemp !== 'undefined'){
        content = contentTemp.find(t => t.id == id)
    }else{
        content = contentList.find(c => c.id == id)
    }

    $('#imageBlock').html('')
    $('#imageBlock').append(
        '<h4>Факты несанкционированной торговли</h4>' +
        '<div class="blokimg">\n' +
        '    <div class="overlay" id="contenedor1">\n' +
        '        <div class="overlay_container">\n' +
        '            <a href="#close">\n' +
        '                <img id="mainImg2" src="' + content.images[0] + '"/>\n' +
        '            </a>\n' +
        '        </div>\n' +
        '    </div>\n' +
        '    <a href="#contenedor1">\n' +
        '        <img class="thumbnail" id="mainImg" src="' + content.images[0] + '" width="650px" height="350px">\n' +
        '    </a>\n' +
        '</div>' +
        '<div id="innerImage" class="row small-up-4">\n' +
        '</div>'
    )
    let images = content.images;
    for(let i=0; i<images.length; i++){
        $('#innerImage').append(
            '<div class="column">\n' +
            '   <img class="thumbnail" id="img' + i + '" src="' + images[i] + '" width="250" height="200"\n' +
            '       onClick="replaceImage(this.id)">\n' +
            '</div>\n'
        )
    }
    $('#imageBlock').append(
        '<div>\n' +
        '   <button type="button" class="submit expanded button">Отправить в надзорный орган</button>\n' +
        '</div>'
    )
}

/*setInterval(update,1000)

function update(){
    /!*stompClient.publish({
        destination: "/app/connection"
    });*!/
    /!*stompClient.deactivate();*!/
}*/
