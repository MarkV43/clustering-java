const url = "http://localhost:8080/";

function status(response) {
    if (response.status >= 200 && response.status < 300) {
        return Promise.resolve(response)
    } else {
        return Promise.reject(new Error(response.statusText))
    }
}

function getPre() {
    const pre = [];
    for (let child of preContainer.children) {
        const obj = {
            name: child.dataset.type
        };
        switch (obj.name) {
            case "sort":
                obj.by = child.children[1].children[1].value;
                obj.order = child.children[2].children[1].value;
                break;
            case "region":
                obj.amount = child.children[1].children[1].value;
                break;
            case "zones":
                break;
            default:
                console.error(`Unknown card type "${obj.name}"`);
                return;
        }

        pre.push(obj);
    }

    return pre;
}

function getPost() {
    const post = [];
    for (let child of postContainer.children) {
        const obj = {
            name: child.dataset.type
        };
        switch (obj.name) {
            case "2in1":
                break;
            case "cutting":
                obj.threshold = child.children[1].children[1].value;
                break;
            default:
                console.error(`Unknown card type "${obj.name}"`);
                return;
        }

        post.push(obj);
    }

    return post;
}

function getClustering() {
    return {
        method: clustering.children[1].children[1].value,
        algorithm: clustering.children[2].children[1].value,
    };
}

function run() {
    const pre = getPre();
    const post = getPost();
    const clustering = getClustering();

    console.log({body: {pre, clustering, post}});

    fetch(url + 'run', {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({pre, clustering, post}),
    }).then(status)
        .then(r => r.json())
        .then(console.log)
        .catch(e => console.error("Request failed.", e));
}

run();