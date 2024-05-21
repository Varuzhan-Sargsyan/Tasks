package org.ip.filter.solutionsForDiscussions

class HashMapMatrix {
    private val map = HashSet<String>()

    fun add(ip: String) {
        map.add(ip)
    }

    fun forEach(action: (String) -> Unit) {
        map.forEach { ip -> action(ip) }
    }
}