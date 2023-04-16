const main = document.getElementsByTagName("main")[0];

window.addEventListener("wheel", function(e) {
    let isTrackpad = false;
    if (e.wheelDeltaY) {
        console.log(e.wheelDeltaY, e.deltaY);
        if (e.wheelDeltaY === -e.deltaY) {
            isTrackpad = true;
        }
    } else if (e.deltaMode === 0) {
        isTrackpad = true;
    }

    if (!isTrackpad) {
        main.scrollLeft += e.deltaY;
        e.preventDefault();
    }
})