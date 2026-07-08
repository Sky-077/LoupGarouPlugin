const http = require("http");

function startKeepAliveServer() {
    const port = process.env.PORT || 3000;

    http.createServer((req, res) => {
        res.writeHead(200, { "Content-Type": "text/plain" });
        res.end("Le Cercle des Loups — bot en ligne");
    }).listen(port, () => {
        console.log(`Serveur keepalive à l'écoute sur le port ${port}`);
    });
}

module.exports = { startKeepAliveServer };
