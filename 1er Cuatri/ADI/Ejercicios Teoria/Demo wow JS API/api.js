class ClienteAPI {
    URL_BASE = 'https://owen-wilson-wow-api.onrender.com/wows/'
    async getRandomWow() {
        var resp = await fetch(this.URL_BASE + 'random')
        var json =  await resp.json()
        return json
    }
    async getWowsMovies(movieName, maxResults) {
        var params = `movie=${movieName}&results=${maxResults}`
        var resp = await fetch(this.URL_BASE + 'random?' + params)
        var json = await resp.json()
        return json
    }

}

export {ClienteAPI}