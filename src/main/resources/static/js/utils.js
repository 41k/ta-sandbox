function formDateTimeString(timestamp, format) {
    return new Date(timestamp).format(format, true);
}

function createDomElement(elementName, classes, id) {
    var element = document.createElement(elementName);
    element.setAttribute('class', classes);
    element.setAttribute('id', id);
    return element;
}

function createDomElementWithInnerHTML(elementName, innerHTML, classes, id) {
    var element = createDomElement(elementName, classes, id);
    element.innerHTML = innerHTML;
    return element;
}

function createDomElementWithChildren(elementName, children, classes, id) {
    var element = createDomElement(elementName, classes, id);
    children.forEach(child => {
        element.appendChild(child);
    });
    return element;
}

function showPopup(popupId) {
    document.getElementById('popup-background').style.display = 'block';
    document.getElementById(popupId).style.display = 'block';
}

function hidePopups() {
    var popups = document.getElementsByClassName('popup');
    for (var i = 0; i < popups.length; i++) {
        popups.item(i).style.display = 'none';
    }
    var contentWrappers = document.getElementsByClassName('popup-content');
    for (var i = 0; i < contentWrappers.length; i++) {
        contentWrappers.item(i).innerHTML = '';
    }
    document.getElementById('popup-background').style.display = 'none';
}

$(document).keyup(function(e) {
    if (e.key === "Escape") {
        hidePopups();
    }
});