import React, { Component } from 'react'
import classnames from 'classnames'
import Loader from '../layout/loader'
import styles from './Page.less'

class Page extends Component {
    render() {
        const {
            className, children, loading = false, inner = false,
        } = this.props
        return (
            <div>
                {children}
            </div>
        )
    }
}

export default Page
